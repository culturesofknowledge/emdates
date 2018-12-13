package nl.knaw.huygens.lobsang.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DateToParse {
  private final String date;

  @JsonCreator
  public DateToParse(@JsonProperty("date") String date) {
    this.date = date;
  }

  public String getDate() {
    return date;
  }
}
