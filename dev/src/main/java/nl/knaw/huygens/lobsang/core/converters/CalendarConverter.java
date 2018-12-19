package nl.knaw.huygens.lobsang.core.converters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.lobsang.api.YearMonthDay;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface CalendarConverter {
  int toRataDie(YearMonthDay date);

  YearMonthDay fromRataDie(int rataDie);
}
