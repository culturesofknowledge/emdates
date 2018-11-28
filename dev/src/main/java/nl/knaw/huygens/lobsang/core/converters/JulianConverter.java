package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

import static java.lang.Math.floor;
import static nl.knaw.huygens.lobsang.core.converters.CalendricalMath.mod;

public class JulianConverter implements CalendarConverter {

  private static final int JULIAN_EPOCH = new GregorianConverter().toRataDie(new YearMonthDay(0, 12, 30));

  @Override
  public int toRataDie(YearMonthDay date) {
    int year = date.getYear();
    int yearValue = year < 0 ? year + 1 : year;

    int month = date.getMonth();
    return (int) (JULIAN_EPOCH - 1 +
        365 * (yearValue - 1) +
        floor((double) (yearValue - 1) / 4) +
        floor((double) (367 * month - 362) / 12) +
        toRataDieCorrection(year, month) + date.getDay());
  }

  private int toRataDieCorrection(int year, int month) {
    // correct for 28- or 29-day Feb
    if (month <= 2) {
      return 0;
    }

    if (isJulianLeapYear(year)) {
      return -1;
    }

    return -2;
  }

  private boolean isJulianLeapYear(int year) {
    int mod = mod(year, 4);
    return year > 0 ? mod == 0 : mod == 3;
  }

  @Override
  public YearMonthDay fromRataDie(int rataDie) {
    int approx = (int) floor((double)(4 * (rataDie - JULIAN_EPOCH) + 1464) / 1461);
    int year = approx <= 0 ? approx - 1 : approx;
    int priorDays = rataDie - toRataDie(new YearMonthDay(year, 1, 1));
    int correction = fromRataDieCorrection(rataDie, year);
    int month = (int) floor((double) (12 * (priorDays + correction) + 373) / 367);
    int day = rataDie - toRataDie(new YearMonthDay(year, month, 1)) + 1;

    return new YearMonthDay(year, month, day);
  }

  private int fromRataDieCorrection(int rataDie, int year) {
    if (rataDie < toRataDie(new YearMonthDay(year, 3, 1))) {
      return 0;
    }

    if (isJulianLeapYear(year)) {
      return 1;
    }

    return 2;
  }

}
