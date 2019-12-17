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

  private ParsedDate(String parsedDate, Set<String> notes, String errorMessage) {
    this.parsedDate = parsedDate;
    this.notes = notes;
    this.errorMessage = errorMessage;
  }

  public static ParsedDate forDate(YearMonthDay parsedDate) {
    return new ParsedDate(parsedDate.asIso8601String(), parsedDate.getNotes(), null);
  }

  public static ParsedDate forError(String message) {
    return new ParsedDate(null, null, message);
  }
}
