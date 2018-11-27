package nl.knaw.huygens.lobsang.core.parsers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.MonthDay;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RomanDateParserTest {
  @ParameterizedTest
  @ValueSource(strings = {"ian", "ian.", "ianuari", "januari", "IanuAri"})
  void januaryParses(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(MonthDay.of(JANUARY, 1), parse(input));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Mar.", "mart.", "martii"})
  void marchParses(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(MonthDay.of(MARCH, 1), parse(input));
  }

  // etc. for Feb, Apr-Dec

  @Test
  void parses() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(MonthDay.of(MARCH, 9), parse("a.d. VII Id. Mart."));
    assertEquals(MonthDay.of(FEBRUARY, 25), parse("A.D. BIS VI KAL. M."));

    Throwable t = assertThrows(nl.knaw.huygens.lobsang.core.parsers.ParseException.class,
      () -> parse("A.D. BIS VI KAL. DEC."));
    assertTrue(t.getMessage().contains("only defined on"));

    t = assertThrows(nl.knaw.huygens.lobsang.core.parsers.ParseException.class,
      () -> parse("A.D. BIS VI ID. Mar."));
    assertTrue(t.getMessage().contains("only defined on"));

    t = assertThrows(nl.knaw.huygens.lobsang.core.parsers.ParseException.class,
      () -> parse("A.D. BIS V KAL. Mar."));
    assertTrue(t.getMessage().contains("only defined on"));
  }

  private MonthDay parse(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    return nl.knaw.huygens.lobsang.core.parsers.RomanDateParser.parse(input);
  }
}
