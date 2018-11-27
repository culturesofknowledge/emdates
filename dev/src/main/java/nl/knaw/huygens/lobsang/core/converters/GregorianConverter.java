package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

import static java.lang.Math.floor;

public class GregorianConverter implements CalendarConverter {
  private static final int LAST_DAY_LEAP_YEAR_400 = 146097;
  private static final int LAST_DAY_LEAP_YEAR_4 = 1461;
  private static final int GREGORIAN_EPOCH = 1; // definition from Calendrical Calculations

  @Override
  public int toRataDie(YearMonthDay date) {
    int year = date.getYear();
    return (int) (GREGORIAN_EPOCH - 1 + 365 * (year - 1) + floor((double) (year - 1) / 4) -
        floor((double) (year - 1) / 100) + floor((double) (year - 1) / 400) +
        floor((double) (367 * date.getMonth() - 362) / 12) + getLeapNumber(date) + date.getDay());
  }

  private int getLeapNumber(YearMonthDay date) {
    if (date.getMonth() <= 2) {
      return 0;
    }

    if (isGregorianLeapYear(date.getYear())) {
      return -1;
    }

    return -2;
  }

  private boolean isGregorianLeapYear(int year) {
    return (mod(year, 4) == 0 && mod(year, 100) != 0) || mod(year, 400) == 0;
  }

  @Override
  public YearMonthDay fromRataDie(int rataDie) {
    int year = gregorianYearFromRataDie(rataDie);
    int priorDays = rataDie - gregorianNewYear(year);
    int correction = getCorrection(rataDie, year);
    int month = (int) floor((double) ( (12 * (priorDays + correction)) + 373) / 367);
    int day = rataDie - toRataDie(new YearMonthDay(year, month, 1)) + 1;

    return new YearMonthDay(year, month, day);
  }

  private int getCorrection(int rataDie, int year) {
    if (rataDie < toRataDie(new YearMonthDay(year, 3, 1))) {
      return 0;
    }

    if (isGregorianLeapYear(year)) {
      return 1;
    }

    return 2;
  }

  private int gregorianNewYear(int year) {
    return toRataDie(new YearMonthDay(year, 1, 1));
  }

  private int gregorianYearFromRataDie(int rataDie) {
    int priorDays = rataDie - GREGORIAN_EPOCH;
    int completed400YearCycles = (int) floor((double) priorDays / LAST_DAY_LEAP_YEAR_400);
    int priorDaysNotIn400 = mod(priorDays, LAST_DAY_LEAP_YEAR_400);
    int cycle100YearNotIn400 = (int) floor((double) priorDaysNotIn400 / 36524);
    int priorDaysNotIn400Or100 = mod(priorDaysNotIn400, 36524);
    int cycle4YearNotIn400Or100 = (int) floor((double) priorDaysNotIn400Or100 / LAST_DAY_LEAP_YEAR_4);
    int priorDaysNotIn400Or100Or4 = mod(priorDaysNotIn400Or100, LAST_DAY_LEAP_YEAR_4);
    int yearsNotIn400Or100Or4 = (int) floor((double) priorDaysNotIn400Or100Or4 / 365);

    int year = 400 * completed400YearCycles + 100 * cycle100YearNotIn400 + 4 * cycle4YearNotIn400Or100 +
        yearsNotIn400Or100Or4;
    return cycle100YearNotIn400 == 4 || yearsNotIn400Or100Or4 == 4 ? year : year + 1;
  }

  // mod function that will always return a positive value, that is more like the lisp mod function
  // see: https://stackoverflow.com/a/4403642
  // see: http://jtra.cz/stuff/lisp/sclr/mod.html
  private int mod(int value, int modBase) {
    while (value < 0) {
      value += modBase;
    }
    int mod = value % modBase;
    return mod;
  }
}
