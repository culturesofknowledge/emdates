package nl.knaw.huygens.lobsang.core.readers;

import com.google.common.base.MoreObjects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class ConversionCsvReader {
  private static final Logger LOG = getLogger(ConversionCsvReader.class);

  private final CSVFormat format;
  private final FieldNames fieldNames;
  private CSVParser parser;

  private ConversionCsvReader(CSVFormat format, FieldNames fieldNames) {
    this.format = format;
    this.fieldNames = fieldNames;
  }

  public FieldNames getFieldNames() {
    return fieldNames;
  }

  public void parse(InputStream inputStream) throws IOException {
    parser = format.parse(new InputStreamReader(inputStream));
  }

  public void validate() {
    if (parser == null) {
      throw new IllegalStateException("parser not yet initialised");
    }

    final Map<String, Integer> headerMap = parser.getHeaderMap();
    fieldNames.stream().forEach(fieldName -> {
      if (!headerMap.containsKey(fieldName)) {
        throw new IllegalStateException(String.format("field '%s' not found in CSV header", fieldName));
      }
    });
  }

  public List<String> getColumnNames() {
    return new ArrayList<>(parser.getHeaderMap().keySet());
  }

  public void read(RecordHandler consumer) throws IOException {
    for (CSVRecord record : parser) {
      consumer.handle(record);
    }
  }

  @FunctionalInterface
  public interface RecordHandler {
    void handle(CSVRecord record) throws IOException;
  }

  public static class Builder {
    static final String DEFAULT_DATE_NAME = "Date";
    static final String DEFAULT_PLACE_NAME = "Place";

    private final Map<String, String> config;

    public Builder(Map<String, String> config) {
      this.config = config;
    }

    public ConversionCsvReader build() {
      final CSVFormat format = CSVFormat.EXCEL;
      applyFormatOption("delimiter", format::withDelimiter);
      applyFormatOption("quoteChar", format::withQuote);
      applyFormatOption("commentStart", format::withCommentMarker);
      applyFormatOption("escape", format::withEscape);
      applyFormatOption("recordSeparator", Function.identity(), format::withRecordSeparator);
      applyFormatOption("nullString", Function.identity(), format::withNullString);
      applyFormatOption("quoteMode", QuoteMode::valueOf, format::withQuoteMode);
      applyFormatOption("ignoreSurroundingSpaces", Boolean::valueOf, format::withIgnoreSurroundingSpaces);
      applyFormatOption("ignoreEmptyLines", Boolean::valueOf, format::withIgnoreEmptyLines);
      applyFormatOption("trim", Boolean::valueOf, format::withTrim);
      applyFormatOption("trailingDelimiter", Boolean::valueOf, format::withTrailingDelimiter);

      final FieldNames fieldNames = new FieldNames(
        config.getOrDefault("dateField", DEFAULT_DATE_NAME),
        config.getOrDefault("placeField", DEFAULT_PLACE_NAME));

      return new ConversionCsvReader(format.withAllowMissingColumnNames().withHeader(), fieldNames);
    }

    private <T> void applyFormatOption(String key, Function<String, T> convertIt,
                                       Function<T, CSVFormat> formatOptionSetter) {
      find(key).ifPresent(convertIt.andThen(logIt(key)).andThen(formatOptionSetter)::apply);
    }

    private void applyFormatOption(String key, Function<Character, CSVFormat> formatOptionSetter) {
      applyFormatOption(key, value -> asSingleChar(key, value), formatOptionSetter);
    }

    private <T> Function<T, T> logIt(String key) {
      return val -> {
        LOG.debug("Setting option {} to {}", key, val);
        return val;
      };
    }

    private Optional<String> find(String key) {
      return Optional.ofNullable(config.get(key));
    }

    private Character asSingleChar(String key, String value) {
      if (value.length() != 1) {
        final String message = String.format("Expected a single character for option '%s', got: [%s]", key, value);
        LOG.debug(message);
        throw new IllegalArgumentException(message);
      }

      return value.charAt(0);
    }
  }

  public static class FieldNames {
    private final String placeFieldName;
    private final String dateField;

    FieldNames(String dateField, String placeFieldName) {
      this.dateField = dateField;
      this.placeFieldName = placeFieldName;
    }


    public String getDateFieldName() {
      return dateField;
    }

    public String getPlaceFieldName() {
      return placeFieldName;
    }

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
}
