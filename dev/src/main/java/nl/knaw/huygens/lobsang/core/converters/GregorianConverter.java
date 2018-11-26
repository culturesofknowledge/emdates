package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

import static java.lang.Math.floor;

public class GregorianConverter implements CalendarConverter {
  private static final int GREGORIAN_EPOCH = 1; // definition from Calendrical Calculations

  @Override
  public int toRataDie(YearMonthDay date) {
    int year = date.getYear();
    return (int) (GREGORIAN_EPOCH - 1 + 365 * (year - 1) + floor((double)(year - 1) / 4) -
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
    return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
  }

  @Override
  public YearMonthDay fromRataDie(int rataDie) {
    throw new UnsupportedOperationException("Yet to be implemented");
  }
}
