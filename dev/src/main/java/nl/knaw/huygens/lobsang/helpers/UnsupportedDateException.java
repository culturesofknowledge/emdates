package nl.knaw.huygens.lobsang.helpers;

import nl.knaw.huygens.lobsang.core.parsers.ParseException;
import nl.knaw.huygens.lobsang.iso8601.UnsupportedIso8601DateException;

public class UnsupportedDateException extends Exception {
  public UnsupportedDateException(String dateString, UnsupportedIso8601DateException e, ParseException pe) {
    super(createErrorMessage(dateString, e, pe));
  }
  private static String createErrorMessage(String dateString, UnsupportedIso8601DateException e, ParseException pe) {
    return String.format(
        "'%s' is an unsupported Iso8601 date: %s \n and '%s' is an unsupported roman date: %s",
        dateString,
        e.getMessage(),
        dateString,
        pe.getMessage()
    );
  }
}
