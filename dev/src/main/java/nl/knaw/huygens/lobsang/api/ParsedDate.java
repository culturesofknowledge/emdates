package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;
import java.util.Set;

public class ParsedDate {
  @JsonProperty
  private final String parsedDate;
  @JsonProperty
  private final Set<String> notes;
  @JsonProperty
  private final String errorMessage;

  private ParsedDate(YearMonthDay parsedDate, String errorMessage) {
    this.parsedDate = parsedDate.asIso8601String();
    this.notes = parsedDate.getNotes();
    this.errorMessage = errorMessage;
  }

  public static ParsedDate forDate(YearMonthDay parsedDate) {
    return new ParsedDate(parsedDate, null);
  }

  public static ParsedDate forError(String message) {
    return new ParsedDate(null, message);
  }
}
