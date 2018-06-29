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

  private CSVParser parser;

  private CsvReader(CSVFormat format) {
    this.format = format;
    LOG.debug("format: {}", format);
  }

  public void parse(InputStream inputStream) throws IOException {
    parser = format.parse(new InputStreamReader(inputStream));
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

    CSVFormat format;

    public Builder(Map<String, String> config) {
      this.config = config;
    }

    public CsvReader build() {
      format = CSVFormat.EXCEL;

      applyOption("delimiter", format::withDelimiter);
      applyOption("quoteChar", format::withQuote);
      applyOption("commentStart", format::withCommentMarker);
      applyOption("escape", format::withEscape);
      applyOption("recordSeparator", Function.identity(), format::withRecordSeparator);
      applyOption("nullString", Function.identity(), format::withNullString);
      applyOption("quoteMode", QuoteMode::valueOf, format::withQuoteMode);
      applyOption("ignoreSurroundingSpaces", Boolean::valueOf, format::withIgnoreSurroundingSpaces);
      applyOption("ignoreEmptyLines", Boolean::valueOf, format::withIgnoreEmptyLines);
      applyOption("trim", Boolean::valueOf, format::withTrim);
      applyOption("trailingDelimiter", Boolean::valueOf, format::withTrailingDelimiter);

      return new CsvReader(format.withAllowMissingColumnNames().withHeader());
    }

    private <T> void applyOption(String key, Function<String, T> convert, Function<T, CSVFormat> setFormatOption) {
      find(key).ifPresent(convert.andThen(peek(key)).andThen(setFormatOption)::apply);
    }

    private void applyOption(String key, Function<Character, CSVFormat> setFormatOption) {
      applyOption(key, value -> asSingleChar(key, value), setFormatOption);
    }

    private <T> Function<T, T> peek(String key) {
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
