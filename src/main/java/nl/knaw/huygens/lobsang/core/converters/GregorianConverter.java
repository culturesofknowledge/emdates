package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

public class GregorianConverter extends JulianGregorianConverterBase {
  @Override
  public int toJulianDay(YearMonthDay date) {
    final int Y = date.getYear();
    final int M = date.getMonth();
    final int D = date.getDay();

    return (
      (1461 * (Y + 4800 + (M - 14) / 12)) / 4 +
        (367 * (M - 2 - 12 * ((M - 14) / 12))) / 12 -
        (3 * ((Y + 4900 + (M - 14) / 12) / 100)) / 4 +
        D - 32075);
  }

  @Override
  public YearMonthDay fromJulianDay(int julianDay) {
    final int J = julianDay;

    // 1. For Gregorian calendar: f = J + j + (((4 × J + B) div 146097) × 3) div 4 + C
    final int f = J + 1401 + (((4 * J + 274277) / 146097) * 3) / 4 + -38;

    return findJulianGregorianDate(f);
  }
}
