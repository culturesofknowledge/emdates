package nl.knaw.huygens.lobsang.core.readers;

import com.google.common.base.MoreObjects;

import java.util.Map;
import java.util.stream.Stream;

public class ConvertFieldNames implements FieldNames {
  private static final String DEFAULT_INPUT_DATE_NAME = "inputDate";
  private static final String DEFAULT_PLACE_ID = "placeId";
  private static final String TARGET_CALENDAR = "targetCalendar";

  private final String placeFieldName;
  private final String targetCalendarField;
  private final String dateField;

  ConvertFieldNames(String dateField, String placeFieldName, String targetCalendarField) {
    this.dateField = dateField;
    this.placeFieldName = placeFieldName;
    this.targetCalendarField = targetCalendarField;
  }

  public static ConvertFieldNames fromConfig(Map<String, String> config) {
    return new ConvertFieldNames(
        config.getOrDefault("dateField", DEFAULT_INPUT_DATE_NAME),
        config.getOrDefault("placeField", DEFAULT_PLACE_ID),
        config.getOrDefault("targetCalendarField", TARGET_CALENDAR)
    );
  }

  public String getDateFieldName() {
    return dateField;
  }

  public String getPlaceFieldName() {
    return placeFieldName;
  }

  public String getTargetCalendarField() {
    return targetCalendarField;
  }

  @Override
  public Stream<String> stream() {
    return Stream.of(dateField, placeFieldName);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("dateFieldName", dateField)
                      .add("placeFieldName", placeFieldName)
                      .toString();
  }
}
