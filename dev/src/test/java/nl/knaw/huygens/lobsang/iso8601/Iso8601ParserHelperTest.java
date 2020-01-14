package nl.knaw.huygens.lobsang.iso8601;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static nl.knaw.huygens.lobsang.iso8601.Iso8601ParserHelper.parse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Iso8601ParserHelperTest {
  // level 0
  @Test
  void year() throws Exception {
    final String dateString = "2019";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }


  @Test
  void yearMonth() throws Exception {
    final String dateString = "2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearMonth() throws Exception {
    final String dateString = "-2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("-2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("-2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearMonthDay() throws Exception {
    final String dateString = "2019-04-19";
    final LocalDate expectedDate = LocalDate.parse(dateString);

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void leapDay() throws Exception {
    final String dateString = "2000-02-29";

    final LocalDate expectedDate = LocalDate.parse("2000-02-29");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void nonLeapYearFeb29() {
    final String dateString = "2019-02-29";

    assertThrows(UnsupportedIso8601DateException.class, () -> parse(dateString));
  }

  @Test
  void yearMonthDayCompact() throws Exception {
    final String dateString = "20190419";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void LeapDayCompact() throws Exception {
    final String dateString = "20000229";

    final LocalDate expectedDate = LocalDate.parse("2000-02-29");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void nonLeapYearFeb29Compact() {
    final String dateString = "20190229";

    assertThrows(UnsupportedIso8601DateException.class, () -> parse(dateString));
  }

  // level 1
  @Test
  void yearUncertain() throws Exception {
    final String dateString = "2019?";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearApproximate() throws Exception {
    final String dateString = "2019~";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearUncertainApproximate() throws Exception {
    final String dateString = "2019%";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthUncertain() throws Exception {
    final String dateString = "2019-04?";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearMonthApproximate() throws Exception {
    final String dateString = "2019-04~";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearMonthUncertainApproximate() throws Exception {
    final String dateString = "2019-04%";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthDayUncertain() throws Exception {
    final String dateString = "2019-04-19?";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void leapYearMonthDayUncertain() throws Exception {
    final String dateString = "2000-02-29?";
    final LocalDate expectedDate = LocalDate.parse("2000-02-29");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }


  @Test
  void yearMonthDayApproximate() throws Exception {
    final String dateString = "2019-04-19~";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void leapYearMonthDayApproximate() throws Exception {
    final String dateString = "2000-02-29~";
    final LocalDate expectedDate = LocalDate.parse("2000-02-29");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearMonthDayUncertainApproximate() throws Exception {
    final String dateString = "2019-04-19%";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void leapYearMonthDayUncertainApproximate() throws Exception {
    final String dateString = "2000-02-29%";
    final LocalDate expectedDate = LocalDate.parse("2000-02-29");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthUnspecifiedDay() throws Exception {
    final String dateString = "2019-04-XX";
    final LocalDate startDate = LocalDate.parse("2019-04-01");
    final LocalDate endDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearMonthUnspecifiedDay() throws Exception {
    final String dateString = "-2019-04-XX";
    final LocalDate startDate = LocalDate.parse("-2019-04-01");
    final LocalDate endDate = LocalDate.parse("-2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearUnspecifiedMonthAndDay() throws Exception {
    final String dateString = "2019-XX-XX";
    final LocalDate startDate = LocalDate.parse("2019-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearUnspecifiedMonthAndDay() throws Exception {
    final String dateString = "-2019-XX-XX";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYearAndMonthAndDay() throws Exception {
    final String dateString = "XXXX-XX-XX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearUnspecifiedMonth() throws Exception {
    final String dateString = "2019-XX";
    final LocalDate startDate = LocalDate.parse("2019-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearUnspecifiedMonth() throws Exception {
    final String dateString = "-2019-XX";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYearAndMonth() throws Exception {
    final String dateString = "XXXX-XX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedSingleYear() throws Exception {
    final String dateString = "201X";
    final LocalDate startDate = LocalDate.parse("2010-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeUnspecifiedSingleYear() throws Exception {
    final String dateString = "-201X";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2010-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedDecadeAndSingleYear() throws Exception {
    final String dateString = "20XX";
    final LocalDate startDate = LocalDate.parse("2000-01-01");
    final LocalDate endDate = LocalDate.parse("2099-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeDecadeAndSingleYear() throws Exception {
    final String dateString = "-20XX";
    final LocalDate startDate = LocalDate.parse("-2099-01-01");
    final LocalDate endDate = LocalDate.parse("-2000-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedCenturyAndDecadeAndSingleYear() throws Exception {
    final String dateString = "2XXX";
    final LocalDate startDate = LocalDate.parse("2000-01-01");
    final LocalDate endDate = LocalDate.parse("2999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeCenturyAndDecadeAndSingleYear() throws Exception {
    final String dateString = "-2XXX";
    final LocalDate startDate = LocalDate.parse("-2999-01-01");
    final LocalDate endDate = LocalDate.parse("-2000-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYear() throws Exception {
    final String dateString = "XXXX";
    final LocalDate startDate = LocalDate.parse("0000-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYear() throws Exception {
    final String dateString = "-XXXX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("-0001-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unparsableDate() {
    final String dateString = "not a date";

    assertThrows(UnsupportedIso8601DateException.class, () -> parse(dateString));
  }

  @Test
  void halfRomanDate() {
    final String dateString = "V. Eid. Quin 1645.";

    assertThrows(UnsupportedIso8601DateException.class, () -> parse(dateString));
  }

  @Test
  void nonValidYearMonthDay() {
    final String dateString = "2019-01-32";

    assertThrows(UnsupportedIso8601DateException.class, () -> parse(dateString));
  }

  @Test
  void nonValidYearMonthDayCompact() {
    final String dateString = "20190132";

    assertThrows(UnsupportedIso8601DateException.class, () -> parse(dateString));
  }




}
