package nl.knaw.huygens.lobsang.core.readers;

import com.google.common.base.MoreObjects;

import java.util.Map;
import java.util.stream.Stream;

public class ConvertFieldNames implements FieldNames {
  private static final String DEFAULT_DATE_NAME = "Date";
  private static final String DEFAULT_PLACE_NAME = "Place";
  private final String placeFieldName;
  private final String dateField;

  ConvertFieldNames(String dateField, String placeFieldName) {
    this.dateField = dateField;
    this.placeFieldName = placeFieldName;
  }

  public static ConvertFieldNames fromConfig(Map<String, String> config) {
    return new ConvertFieldNames(
        config.getOrDefault("dateField", DEFAULT_DATE_NAME),
        config.getOrDefault("placeField", DEFAULT_PLACE_NAME)
    );
  }


  public String getDateFieldName() {
    return dateField;
  }

  public String getPlaceFieldName() {
    return placeFieldName;
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
