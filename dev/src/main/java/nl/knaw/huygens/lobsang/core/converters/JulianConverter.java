package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

import static java.lang.Math.floor;
import static nl.knaw.huygens.lobsang.core.converters.CalendricalMath.mod;

public class JulianConverter implements CalendarConverter {
  @Override
  public int toRataDie(YearMonthDay date) {
    int julianEpoch = new GregorianConverter().toRataDie(new YearMonthDay(0, 12, 30));
    int year = date.getYear();
    int yearValue = year < 0 ? year + 1 : year;

    int month = date.getMonth();
    return (int) (julianEpoch - 1 +
        365 * (yearValue - 1) +
        floor((double) (yearValue - 1) / 4) +
        floor((double) (367 * month - 362) / 12) +
        correction(year, month) + date.getDay());
  }

  private int correction(int year, int month) {
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
    throw new UnsupportedOperationException("Yet to be implemented");
  }
}
