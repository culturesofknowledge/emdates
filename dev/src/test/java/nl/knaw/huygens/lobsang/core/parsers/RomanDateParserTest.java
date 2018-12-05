package nl.knaw.huygens.lobsang.core.parsers;

import nl.knaw.huygens.lobsang.api.YearMonthDay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static nl.knaw.huygens.lobsang.core.parsers.RomanDateParser.DEFAULT_YEAR_IF_UNPARSABLE;
import static nl.knaw.huygens.lobsang.core.parsers.RomanDateParser.ILLEGAL_ROMAN_NUMERAL;
import static nl.knaw.huygens.lobsang.core.parsers.RomanDateParser.INVALID_ROMAN_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RomanDateParserTest {
  @ParameterizedTest
  @ValueSource(strings = {"ian", "ian.", "ianuari", "januari", "IanuAri"})
  void januaryParses(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(new YearMonthDay(0, 1, 1), parse(input));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Mar.", "mart.", "martii"})
  void marchParses(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(new YearMonthDay(0, 3, 1), parse(input));
  }

  // etc. for Feb, Apr-Dec

  @Test
  void parses() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    assertEquals(new YearMonthDay(0, 3, 9), parse("a.d. VII Id. Mart."));
    assertEquals(new YearMonthDay(0, 2, 25), parse("A.D. BIS VI KAL. M."));

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
    assertEquals(new YearMonthDay(1645, 7, 11), parse("V. Eid. Quin 1645."));
    assertEquals(new YearMonthDay(1645, 7, 11), parse("V. Eid. Quin. 1645."));
    assertEquals(new YearMonthDay(1645, 7, 11), parse("V. Eid. Quintil. 1645."));
    assertEquals(new YearMonthDay(1645, 7, 11), parse("V. Eid. Qvinctil. 1645."));

    assertEquals(new YearMonthDay(1646, 5, 21), parse("XXI Maj 1646"));

    // Hagae Comitis meaning "sent from The Hague"
    assertEquals(new YearMonthDay(1646, 9, 1), parse("Hagae Comitis Kal. Septemb. 1646."));
    // If scholar omits placename, but leaves 'Comitis'
    assertEquals(new YearMonthDay(1646, 9, 1), parse("Comitis Kal. Septemb. 1646."));

    assertEquals(new YearMonthDay(1646, 10, 31), parse("pridie Kal. Nouemb. 1646."));

    assertEquals(new YearMonthDay(1647, 3, 4), parse("IV. Nonis Mart. 1647."));

    // this read 'X. Kal. Jan. A.S. MDCLVII.' TODO: deal with 'A.S.' (?= 'A.D.')
    assertEquals(new YearMonthDay(1657, 12, 23), parse("X. Kal. Jan. MDCLVII."));

    // Originally read "V Non. Feb. 1657.", but this is actually an "illegal date".
    // Februari 2 would have been "IV Non. Feb."
    assertEquals(new YearMonthDay(1657, 2, 2), parse("IV Non. Feb. 1657."));
    Throwable t = assertThrows(nl.knaw.huygens.lobsang.core.parsers.ParseException.class,
      () -> parse("V Non. Feb. 1657."));
    assertTrue(t.getMessage().toLowerCase().contains("unrecognised roman date"));

    assertEquals(new YearMonthDay(1658, 8, 1), parse("Kal. Augusti 1658"));
    assertEquals(new YearMonthDay(1661, 5, 12), parse("XII Maij 1661"));
    assertEquals(new YearMonthDay(1663, 1, 2), parse("IV Nonas Januarias 1663"));
    assertEquals(new YearMonthDay(1663, 10, 9), parse("VII Eid. Octobr. MDCLXIII."));
    assertEquals(new YearMonthDay(1664, 4, 30), parse("prid. Kal. Maias 1664."));
    assertEquals(new YearMonthDay(1664, 8, 23), parse("X, Kal. Septemb. 1664."));
    assertEquals(new YearMonthDay(1669, 2, 1), parse("Kalendis februarij 1669."));

    // Scaliger
    assertEquals(new YearMonthDay(1593, 4, 28), parse("Iv Kalend. Maias MDXCIII."));
  }

  @Test
  void illegalRomanNumeralInYearProducesWarning() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    // "cDDcLXII" is not a roman numeral. Expect complaints and default year
    final YearMonthDay result = parse("a.d. VI. Kal. April. cDDcLXII");
    assertTrue(result.getNotes().stream().anyMatch(s -> s.startsWith(ILLEGAL_ROMAN_NUMERAL)));
    assertEquals(new YearMonthDay(DEFAULT_YEAR_IF_UNPARSABLE, 3, 27), result);
  }

  @Test
  void illegalRomanNumeralInCountProducesWarning() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    final YearMonthDay result = parse("a.d. IIX Kal. April. MDCLVII");
    assertTrue(result.getNotes().stream().anyMatch(s -> s.startsWith(ILLEGAL_ROMAN_NUMERAL)));
    assertEquals(new YearMonthDay(1657, 3, 30), result); // III Kal. April = 30 March used instead
  }

  @Test
  // legal roman numeral used in count, but invalid in the context of "a.d. <count> <event>" notation
  void invalidRomanNumeralInCountProducesWarning() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    final YearMonthDay result = parse("a.d. MDC Kal. April. MDCLVII");
    assertTrue(result.getNotes().stream().anyMatch(s -> s.startsWith(INVALID_ROMAN_COUNT)));
    assertEquals(new YearMonthDay(1657, 3, 30), result); // III Kal. April = 30 March used instead
  }

  @Test
  void multipleRomanNumeralIssuesAccumulateAsWarnings() throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    final YearMonthDay result = parse("a.d. MDC Kal. April. cDDcLXII");
    assertTrue(result.getNotes().stream().anyMatch(s -> s.startsWith(INVALID_ROMAN_COUNT) && s.contains("MDC")));
    assertTrue(result.getNotes().stream().anyMatch(s -> s.startsWith(ILLEGAL_ROMAN_NUMERAL) && s.contains("cDDcLXII")));
    assertEquals(2, result.getNotes().size()); // no other issues
  }

  @ParameterizedTest
  @ValueSource(strings = {"mdclxii", "MDCLXII", "mDcLxIi", "MdClXiI"})
  void romanNumeralParsingIgnoresCase(String year) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    // Expect robustness to random uppercase / lowercase changes.
    assertEquals(new YearMonthDay(1662, 3, 27), parse("a.d. VI. Kal. April. " + year));
  }

  private YearMonthDay parse(final String input) throws nl.knaw.huygens.lobsang.core.parsers.ParseException {
    return nl.knaw.huygens.lobsang.core.parsers.RomanDateParser.parse(input);
  }
}
