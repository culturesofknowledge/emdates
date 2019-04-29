package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParsedDate {
  @JsonProperty
  private final String parsedDate;
  @JsonProperty
  private final String errorMessage;

  private ParsedDate(YearMonthDay parsedDate, String errorMessage) {

    this.parsedDate = parsedDate.asIso8601String();
    this.errorMessage = errorMessage;
  }

  public static ParsedDate forDate(YearMonthDay parsedDate) {
    return new ParsedDate(parsedDate, null);
  }


  public static ParsedDate forError(String message) {
    return new ParsedDate(null, message);
  }
}
