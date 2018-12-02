package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

public interface CalendarConverter {
  int toRataDie(YearMonthDay date);

  YearMonthDay fromRataDie(int rataDie);
}
