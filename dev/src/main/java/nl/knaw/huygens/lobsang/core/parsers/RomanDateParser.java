package nl.knaw.huygens.lobsang.core.parsers;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;

public class RomanDateParser {
  private static final Logger LOG = LoggerFactory.getLogger(RomanDateParser.class);

  private final Map<String, MonthDay> romanDates = new HashMap<>();

  public RomanDateParser() {
    int month = 1;
    int day = 1;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/roman.txt")))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (Strings.isNullOrEmpty(line)) {
          day = 1;
          month++;
        }
        else {
          // escape hatch for leap year day to not advance the day counter
          if (line.charAt(0) == '!') {
            romanDates.put(line.substring(1), MonthDay.of(month, day));
          }
          else {
            romanDates.put(line, MonthDay.of(month, day));
            day++;
          }
        }
      }
    } catch (IOException e) {
      LOG.warn("Failed to read roman dates file");
    }
  }

  public MonthDay parse(String date) {
    return romanDates.get(date);
  }
}
