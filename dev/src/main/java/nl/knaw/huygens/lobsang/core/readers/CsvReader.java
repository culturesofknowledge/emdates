package nl.knaw.huygens.lobsang.core.readers;

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

import static org.slf4j.LoggerFactory.getLogger;

public class CsvReader {
  private static final Logger LOG = getLogger(CsvReader.class);

  private final CSVFormat format;
  private final FieldNames fieldNames;
  private CSVParser parser;

  private CsvReader(CSVFormat format, FieldNames fieldNames) {
    this.format = format;
    this.fieldNames = fieldNames;
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

    private final Map<String, String> config;
    private FieldNames fieldNames;

    public Builder(Map<String, String> config, FieldNames fieldNames) {
      this.config = config;
      this.fieldNames = fieldNames;
    }

    public CsvReader build() {
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

      return new CsvReader(format.withAllowMissingColumnNames().withHeader(), fieldNames);
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

}
