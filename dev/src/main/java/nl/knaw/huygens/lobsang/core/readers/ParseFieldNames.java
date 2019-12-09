package nl.knaw.huygens.lobsang.core.readers;

import com.google.common.base.MoreObjects;

import java.util.stream.Stream;

public class ParseFieldNames implements FieldNames {
  private final String idField;
  private final String dateField;

  public ParseFieldNames() {
    this("Id", "Date");
  }

  ParseFieldNames(String idField, String dateField) {
    this.idField = idField;
    this.dateField = dateField;
  }


  public String getDateFieldName() {
    return dateField;
  }

  public String getIdFieldName() {
    return idField;
  }

  @Override
  public Stream<String> stream() {
    return Stream.of(idField, dateField);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("idFieldName", idField)
                      .add("dateFieldName", dateField)
                      .toString();
  }

}
