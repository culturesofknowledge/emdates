package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

public class JulianConverter extends JulianGregorianConverterBase {
  @Override
  public int toJulianDay(YearMonthDay date) {
    final int Y = date.getYear();
    final int M = date.getMonth();
    final int D = date.getDay();

    return (
      367 * Y -
        (7 * (Y + 5001 + (M - 9) / 7)) / 4 +
        (275 * M) / 9 +
        D + 1729777);
  }

  @Override
  public YearMonthDay fromJulianDay(int julianDay) {
    final int J = julianDay;

    // 1. For Julian calendar: f = J + j
    final int f = J + 1401;

    return findJulianGregorianDate(f);
  }
}
