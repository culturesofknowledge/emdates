package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Period during which a given calendar is actively used
 */
public class CalendarPeriod {

  private final String calendar;
  private final String startDate;
  private final String endDate;
  private final String provenance;

  @JsonCreator
  public CalendarPeriod(@JsonProperty("calendar") String calendar,
                        @JsonProperty("start") String startDate,
                        @JsonProperty("end") String endDate,
                        @JsonProperty("provenance") String provenance) {
    this.calendar = calendar;
    this.startDate = startDate;
    this.endDate = endDate;
    this.provenance = provenance;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CalendarPeriod that = (CalendarPeriod) o;

    return new EqualsBuilder()
        .append(calendar, that.calendar)
        .append(startDate, that.startDate)
        .append(endDate, that.endDate)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(calendar)
        .append(startDate)
        .append(endDate)
        .toHashCode();
  }
}
