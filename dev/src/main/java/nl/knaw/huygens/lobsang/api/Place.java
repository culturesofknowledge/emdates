package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Place {
  private final String name;
  private final String placeId;
  private final Optional<String> parent;
  private final List<CalendarPeriod> calendarPeriods;
  private final List<StartOfYear> startOfYearList;

  @JsonCreator
  public Place(@JsonProperty("name") String name,
               @JsonProperty("placeId") String placeId,
               @JsonProperty("parent") String parent,
               @JsonProperty("calendarPeriods") List<CalendarPeriod> calendarPeriods,
               @JsonProperty("startOfYear") List<StartOfYear> startOfYearList) {

    this.name = name;
    Preconditions.checkArgument(!StringUtils.isEmpty(placeId), "'placeId' cannot be null or empty");
    this.placeId = placeId;
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

  public String getPlaceId() {
    return placeId;
  }

  public Optional<String> getParent() {
    return parent;
  }
}
