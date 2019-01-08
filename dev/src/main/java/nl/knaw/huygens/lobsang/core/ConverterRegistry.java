package nl.knaw.huygens.lobsang.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;

import java.util.Map;
import java.util.Optional;

public class ConverterRegistry {
  private final Map<String, CalendarConverter> convertersByType;
  private final CalendarConverter defaultConverter;

  @JsonCreator
  public ConverterRegistry(@JsonProperty("knownCalendars") Map<String, CalendarConverter> convertersByType,
                           @JsonProperty("default") CalendarConverter defaultConverter) {
    this.convertersByType = convertersByType;
    this.defaultConverter = defaultConverter;
  }

  public Optional<CalendarConverter> get(String type) {
    CalendarConverter exactMatch = convertersByType.get(type.toLowerCase());
    if (exactMatch != null) {
      return Optional.of(exactMatch);
    }

    // see if we can find a calendar that has a similar name
    return convertersByType.entrySet().stream()
                           .filter((entry) -> isCloseEnough(entry.getKey(), type.toLowerCase()))
                           .map(Map.Entry::getValue)
                           .findFirst();

  }

  private boolean isCloseEnough(String calendar, String requestedCalendar) {
    return calendar.contains(requestedCalendar) || requestedCalendar.contains(calendar);
  }

  public CalendarConverter defaultConverter() {
    return defaultConverter;
  }
}
