package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Place {
  private String name;
  private List<CalendarPeriod> calendarPeriods;
  private List<StartOfYear> startOfYearList = new ArrayList<>();

  @JsonProperty
  public String getName() {
    return name;
  }

  @JsonProperty("calendarPeriods")
  public List<CalendarPeriod> getCalendarPeriods() {
    return calendarPeriods;
  }

  @JsonProperty("startOfYear")
  public List<StartOfYear> getStartOfYearList() {
    return startOfYearList;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("name", name)
      .add("calendarPeriods", calendarPeriods)
      .add("startOfYearList", startOfYearList)
      .toString();
  }

}
