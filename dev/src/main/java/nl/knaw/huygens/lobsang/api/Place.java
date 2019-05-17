package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Place {
  private String name;
  private List<CalendarPeriod> calendarPeriods;
  private List<StartOfYear> startOfYearList;

  @JsonCreator
  public Place(@JsonProperty("name") String name,
               @JsonProperty("calendarPeriods") List<CalendarPeriod> calendarPeriods,
               @JsonProperty("startOfYear") List<StartOfYear> startOfYearList) {

    this.name = name;
    this.calendarPeriods = calendarPeriods;
    this.startOfYearList = startOfYearList == null ? Lists.newArrayList() : startOfYearList;
  }

  public String getName() {
    return name;
  }

  public List<CalendarPeriod> getCalendarPeriods() {
    return calendarPeriods;
  }

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
