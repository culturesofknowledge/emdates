package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConverterRegistry {
  private static final String DEFAULT_CALENDAR_CONVERTER_NAME = "default";

  private final Map<String, CalendarConverter> convertersByType = new HashMap<>();

  public Set<String> list() {
    return convertersByType.keySet();
  }

  public CalendarConverter get(String type) {
    return convertersByType.get(type.toLowerCase());
  }

  public void register(String type, CalendarConverter converter) {
    convertersByType.put(checkNotNull(type).toLowerCase(), checkNotNull(converter));
  }

  public CalendarConverter defaultConverter() {
    return Optional.ofNullable(convertersByType.get(DEFAULT_CALENDAR_CONVERTER_NAME))
                   .orElseThrow(() -> new NoSuchElementException("No 'default' calendar registered"));
  }
}
