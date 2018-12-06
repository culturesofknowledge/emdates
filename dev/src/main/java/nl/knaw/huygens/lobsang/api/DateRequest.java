package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.MoreObjects.toStringHelper;

public class DateRequest {
  private static final Logger LOG = LoggerFactory.getLogger(DateRequest.class);

  private int year;
  private int month;
  private int day;
  private String placeTerms;
  private String targetCalendar; // convert date to which calendar?

  public DateRequest() {
    // Jackson deserialization
  }

  public DateRequest(int year, int month, int day) {
    this(year, month, day, null, null);
  }

  public DateRequest(int year, int month, int day, String placeTerms, String targetCalendar) {
    this.year = year;
    this.month = month;
    this.day = day;
    this.placeTerms = placeTerms;
    this.targetCalendar = targetCalendar;
  }

  @JsonProperty
  public int getYear() {
    return year;
  }

  @JsonProperty
  public int getMonth() {
    return month;
  }

  @JsonProperty
  public int getDay() {
    return day;
  }

  @JsonProperty("place")
  public String getPlaceTerms() {
    return placeTerms;
  }

  @JsonProperty
  public String getTargetCalendar() {
    return targetCalendar;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("year", year)
      .add("month", month)
      .add("day", day)
      .add("placeTerms", placeTerms)
      .add("targetCalendar", targetCalendar)
      .toString();
  }

  public YearMonthDay asYearMonthDay() {
    return new YearMonthDay(getYear(), getMonth(), getDay());
  }
}
