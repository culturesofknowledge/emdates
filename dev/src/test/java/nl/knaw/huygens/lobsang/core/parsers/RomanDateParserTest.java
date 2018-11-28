package nl.knaw.huygens.lobsang.core.parsers;

import nl.knaw.huygens.lobsang.api.YearMonthDay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RomanDateParserTest {
  @ParameterizedTest
  @ValueSource(strings = {"ian", "ian.", "ianuari", "januari", "IanuAri"})
  void januaryParses(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(new YearMonthDay(-1, 1, 1), parse(input));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Mar.", "mart.", "martii"})
  void marchParses(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(new YearMonthDay(-1, 3, 1), parse(input));
  }

  // etc. for Feb, Apr-Dec

  @Test
  void parses() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(new YearMonthDay(-1, 3, 9), parse("a.d. VII Id. Mart."));
    assertEquals(new YearMonthDay(-1, 2, 25), parse("A.D. BIS VI KAL. M."));

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

  @Test
  void inTheWild() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    // this reads 'Comitis Kal. Septemb. 1646.' TODO: deal with 'Comitis'
    assertEquals(new YearMonthDay(1646, 9, 1), parse("Kal. Septemb. 1646."));

    assertEquals(new YearMonthDay(1646, 10, 31), parse("pridie Kal. Nouemb. 1646."));
    assertEquals(new YearMonthDay(1647, 3, 4), parse("IV. Nonis Mart. 1647."));

    // this read 'X. Kal. Jan. A.S. MDCLVII.' TODO: deal with 'A.S.' (?= 'A.D.')
    assertEquals(new YearMonthDay(1657, 12, 23), parse("X. Kal. Jan. MDCLVII."));

    // Originally read "V Non. Feb. 1657.", but this is actually an "illegal date".
    // Februari 2 would have been "IV Non. Feb."
    assertEquals(new YearMonthDay(1657, 2, 2), parse("IV Non. Feb. 1657."));
    assertEquals(new YearMonthDay(1658, 8, 1), parse("Kal. Augusti 1658"));
    assertEquals(new YearMonthDay(1646, 5, 21), parse("XXI Maj 1646"));
    assertEquals(new YearMonthDay(1663, 1, 2), parse("IV Nonas Januarias 1663"));
    assertEquals(new YearMonthDay(1657, 12, 23), parse("X. Kal. Jan. MDCLVII"));
  }

  private YearMonthDay parse(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    return nl.knaw.huygens.lobsang.core.parsers.RomanDateParser.parse(input);
  }
}
