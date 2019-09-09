package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Place {
  private final String name;
  private final Optional<String> geoNamesId;
  private final Optional<String> parent;
  private final List<CalendarPeriod> calendarPeriods;
  private final List<StartOfYear> startOfYearList;

  @JsonCreator
  public Place(@JsonProperty("name") String name,
               @JsonProperty("geoNamesID") String geoNamesId,
               @JsonProperty("parent") String parent,
               @JsonProperty("calendarPeriods") List<CalendarPeriod> calendarPeriods,
               @JsonProperty("startOfYear") List<StartOfYear> startOfYearList) {

    this.name = name;
    this.geoNamesId = Optional.ofNullable(geoNamesId);
    this.parent = Optional.ofNullable(parent);
    this.calendarPeriods = calendarPeriods == null ? Lists.newArrayList() : calendarPeriods;
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

  public Optional<String> getGeoNamesId() {
    return geoNamesId;
  }

  public Optional<String> getParent() {
    return parent;
  }
}
