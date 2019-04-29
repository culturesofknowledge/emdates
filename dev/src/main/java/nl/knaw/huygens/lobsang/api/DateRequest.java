package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.iso8601.Iso8601Date;
import nl.knaw.huygens.lobsang.iso8601.Iso8601ParserHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static com.google.common.base.MoreObjects.toStringHelper;

public class DateRequest {
  private static final Logger LOG = LoggerFactory.getLogger(DateRequest.class);

  private String placeTerms;
  private String targetCalendar; // convert date to which calendar?
  private String date;

  public DateRequest() {
    // Jackson deserialization
  }

  public DateRequest(String date, String placeTerms, String targetCalendar) {
    this.date = date;
    this.placeTerms = placeTerms;
    this.targetCalendar = targetCalendar;
  }

  @JsonProperty
  public String getDate() {
    return date;
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
      .add("date", date)
      .add("placeTerms", placeTerms)
      .add("targetCalendar", targetCalendar)
      .toString();
  }

  public Iso8601Date asIso8601Date() {
    return Iso8601ParserHelper.parse(getDate());
  }
}
