package nl.knaw.huygens.lobsang.helpers;

import nl.knaw.huygens.lobsang.iso8601.Iso8601Date;
import nl.knaw.huygens.lobsang.iso8601.Uncertainty;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DateStringParserTest {
  @Test
  void createsAnIso8601DateFromSupportedIso8601DateString() throws Exception {

    final Iso8601Date iso8601Date = DateStringParser.parse("2011-12-13");

    assertThat(iso8601Date.getStart(), is(LocalDate.of(2011, 12, 13)));
    assertThat(iso8601Date.getEnd(), is(LocalDate.of(2011, 12, 13)));
    assertThat(iso8601Date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void createsAnIso8601DateFromSupportedRomanDateString() throws Exception {

    final Iso8601Date iso8601Date = DateStringParser.parse("IIII Idus Decemb. M D LXIIII.");

    assertThat(iso8601Date.getStart(), is(LocalDate.of(1564, 12, 10)));
    assertThat(iso8601Date.getEnd(), is(LocalDate.of(1564, 12, 10)));
    assertThat(iso8601Date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void createsAnIso8601DateFromSupportedHalfRomanDateString() throws Exception {

    final Iso8601Date iso8601Date = DateStringParser.parse("V. Eid. Quin 1645.");

    assertThat(iso8601Date.getStart(), is(LocalDate.of(1645, 07, 11)));
    assertThat(iso8601Date.getEnd(), is(LocalDate.of(1645, 07, 11)));
    assertThat(iso8601Date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void throwsAnExceptionWhenTheStringNeitherSupportedIso8601NorSupportedRoman() {
    Exception exception = assertThrows(UnsupportedDateException.class, () -> DateStringParser.parse("Not a date"));

    assertThat(exception.getMessage(), allOf(containsStringIgnoringCase("unsupported iso8601 date"),
        containsStringIgnoringCase("unsupported roman date")));
  }

  @Test
  void throwsAnExceptionWhenDateIsNonValidISO8601Date() {
    Exception exception = assertThrows(UnsupportedDateException.class, () -> DateStringParser.parse("0000-01-32"));

    assertThat(exception.getMessage(), allOf(containsStringIgnoringCase("unsupported iso8601 date"),
        containsStringIgnoringCase("unsupported roman date")));
  }

}
