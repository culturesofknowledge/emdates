package nl.knaw.huygens.lobsang.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConverterRegistry {
  private static final String DEFAULT_CALENDAR_CONVERTER_NAME = "default";
  private final Map<String, CalendarConverter> convertersByType;

  @JsonCreator
  public ConverterRegistry(@JsonProperty("knownCalendars") Map<String, CalendarConverter> convertersByType) {
    this.convertersByType = convertersByType;
  }

  public Optional<CalendarConverter> get(String type) {
    return Optional.ofNullable(convertersByType.get(type.toLowerCase()));
  }

  public CalendarConverter defaultConverter() {
    return Optional.ofNullable(convertersByType.get(DEFAULT_CALENDAR_CONVERTER_NAME))
                   .orElseThrow(() -> new NoSuchElementException("No 'default' calendar registered"));
  }
}
