package nl.knaw.huygens.lobsang.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

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
    return Optional.ofNullable(convertersByType.get(type.toLowerCase()));
  }

  public CalendarConverter defaultConverter() {
    return defaultConverter;
  }
}
