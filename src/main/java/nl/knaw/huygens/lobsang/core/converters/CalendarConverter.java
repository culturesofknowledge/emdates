package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

public interface CalendarConverter {
  int toJulianDay(YearMonthDay date);

  YearMonthDay fromJulianDay(int julianDay);
}
