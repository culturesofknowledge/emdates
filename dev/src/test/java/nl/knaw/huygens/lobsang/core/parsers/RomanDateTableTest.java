package nl.knaw.huygens.lobsang.core.parsers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.MonthDay;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RomanDateTableTest {
  private static RomanDateTable PARSER;

  @BeforeAll
  static void setUp() {
    PARSER = new RomanDateTable();
  }

  private static MonthDay parse(String date) {
    return PARSER.lookup(date);
  }

  private static MonthDay parseIdes(String month) {
    return parse(String.format("ID. %s.", month));
  }

  private static MonthDay parsePridIdes(String month) {
    return parse(String.format("PRID. ID. %s.", month));
  }

  private static MonthDay parseNones(String month) {
    return parse(String.format("NON. %s.", month));
  }

  private static MonthDay parsePridNones(String month) {
    return parse(String.format("PRID. NON. %s.", month));
  }

  private static MonthDay parseKalends(String month) {
    return parse(String.format("KAL. %s.", month));
  }

  private static MonthDay parsePridKalends(String month) {
    return parse(String.format("PRID. KAL. %s.", month));
  }

  @Test
  void january_first() {
    assertEquals(MonthDay.of(1, 1), parse("KAL. IAN."));
  }

  @Test
  void leap_year() {
    assertAll("Leap Year",
      () -> assertEquals(MonthDay.of(2, 24), parse("A.D. VI KAL. MART.")),
      () -> assertEquals(MonthDay.of(2, 25), parse("A.D. BIS VI KAL. M.")),
      () -> assertEquals(MonthDay.of(2, 25), parse("A.D. V KAL. MART.")),
      () -> assertEquals(MonthDay.of(2, 26), parse("A.D. IV KAL. MART.")),
      () -> assertEquals(28, parsePridKalends("MART").getDayOfMonth()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"IAN", "FEB", "APR", "IVN", "AVG", "SEPT", "NOV", "DEC"})
  void ides_on_day_13(String month) {
    assertEquals(13, parseIdes(month).getDayOfMonth());
  }

  @ParameterizedTest
  @ValueSource(strings = {"MART", "MAI", "IVL", "OCT"})
  void ides_on_day_15(String month) {
    assertEquals(15, parseIdes(month).getDayOfMonth());
  }

  @ParameterizedTest
  @ValueSource(strings = {"IAN", "FEB", "MART", "APR", "MAI", "IVN", "IVL", "AVG", "SEPT", "OCT", "NOV", "DEC"})
  void kalends_on_day_1(String month) {
    assertEquals(1, parseKalends(month).getDayOfMonth());
  }

  @ParameterizedTest
  @ValueSource(strings = {"IAN", "FEB", "MART", "APR", "MAI", "IVN", "IVL", "AVG", "SEPT", "OCT", "NOV", "DEC"})
  void ides_8_days_after_nones(String month) {
    final int ides = parseIdes(month).getDayOfMonth();
    final int nones = parseNones(month).getDayOfMonth();
    assertEquals(8, ides - nones);
  }

  @ParameterizedTest
  @ValueSource(strings = {"IAN", "FEB", "MART", "APR", "MAI", "IVN", "IVL", "AVG", "SEPT", "OCT", "NOV", "DEC"})
  void prid_1_day_before_actual(String month) {
    final int ides = parseIdes(month).getDayOfMonth();
    final int pridIdes = parsePridIdes(month).getDayOfMonth();
    final int nones = parseNones(month).getDayOfMonth();
    final int pridNones = parsePridNones(month).getDayOfMonth();
    assertAll("PRID. {NONES,IDES} before actual {NONES,IDES}",
      () -> assertEquals(1, ides - pridIdes),
      () -> assertEquals(1, nones - pridNones)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"MAI", "IVL", "OCT", "DEC"})
  void prid_kal_falls_on_day_30(String month) {
    // e.g., "PRID. KAL. MAI" = day before 1st of May = 30 April
    assertEquals(30, parsePridKalends(month).getDayOfMonth(), () -> "PRID. KAL. " + month);
  }

  @ParameterizedTest
  @ValueSource(strings = {"IAN", "FEB", "APR", "IVN", "AVG", "SEPT", "NOV"})
  void prid_kal_falls_on_day_31(String month) {
    // e.g., "PRID. KAL. NOV" = day before 1st of November = 31 October
    assertEquals(31, parsePridKalends(month).getDayOfMonth(), () -> "PRID. KAL. " + month);
  }
}
