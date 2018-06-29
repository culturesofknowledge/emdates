package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Period during which a given calendar is actively used
 */
public class CalendarPeriod {
  @JsonProperty("calendar")
  private String calendar;

  @JsonProperty("start")
  private String startDate;

  @JsonProperty("end")
  private String endDate;

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
