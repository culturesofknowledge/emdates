package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Period during which a given calendar is actively used
 */
public class CalendarPeriod {

  private final String calendar;

  private final String startDate;

  private final String endDate;

  @JsonCreator
  public CalendarPeriod(@JsonProperty("calendar") String calendar,
                        @JsonProperty("start") String startDate,
                        @JsonProperty("end") String endDate) {
    this.calendar = calendar;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getCalendar() {
    return calendar;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("calendar", calendar)
      .add("startDate", startDate)
      .add("endDate", endDate)
      .toString();
  }
}
