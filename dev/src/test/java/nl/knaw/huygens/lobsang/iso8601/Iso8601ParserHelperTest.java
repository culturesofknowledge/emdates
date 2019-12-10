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
  void year() {
    final String dateString = "2019";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }


  @Test
  void yearMonth() {
    final String dateString = "2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearMonth() {
    final String dateString = "-2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("-2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("-2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearMonthDay() {
    final String dateString = "2019-04-19";
    final LocalDate expectedDate = LocalDate.parse(dateString);

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearMonthDayCompact() {
    final String dateString = "20190419";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  // level 1
  @Test
  void yearUncertain() {
    final String dateString = "2019?";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearApproximate() {
    final String dateString = "2019~";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearUncertainApproximate() {
    final String dateString = "2019%";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthUncertain() {
    final String dateString = "2019-04?";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearMonthApproximate() {
    final String dateString = "2019-04~";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearMonthUncertainApproximate() {
    final String dateString = "2019-04%";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthDayUncertain() {
    final String dateString = "2019-04-19?";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearMonthDayApproximate() {
    final String dateString = "2019-04-19~";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearMonthDayUncertainApproximate() {
    final String dateString = "2019-04-19%";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthUnspecifiedDay() {
    final String dateString = "2019-04-XX";
    final LocalDate startDate = LocalDate.parse("2019-04-01");
    final LocalDate endDate = LocalDate.parse("2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearMonthUnspecifiedDay() {
    final String dateString = "-2019-04-XX";
    final LocalDate startDate = LocalDate.parse("-2019-04-01");
    final LocalDate endDate = LocalDate.parse("-2019-04-30");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearUnspecifiedMonthAndDay() {
    final String dateString = "2019-XX-XX";
    final LocalDate startDate = LocalDate.parse("2019-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearUnspecifiedMonthAndDay() {
    final String dateString = "-2019-XX-XX";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYearAndMonthAndDay() {
    final String dateString = "XXXX-XX-XX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearUnspecifiedMonth() {
    final String dateString = "2019-XX";
    final LocalDate startDate = LocalDate.parse("2019-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearUnspecifiedMonth() {
    final String dateString = "-2019-XX";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYearAndMonth() {
    final String dateString = "XXXX-XX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedSingleYear() {
    final String dateString = "201X";
    final LocalDate startDate = LocalDate.parse("2010-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeUnspecifiedSingleYear() {
    final String dateString = "-201X";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2010-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedDecadeAndSingleYear() {
    final String dateString = "20XX";
    final LocalDate startDate = LocalDate.parse("2000-01-01");
    final LocalDate endDate = LocalDate.parse("2099-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeDecadeAndSingleYear() {
    final String dateString = "-20XX";
    final LocalDate startDate = LocalDate.parse("-2099-01-01");
    final LocalDate endDate = LocalDate.parse("-2000-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedCenturyAndDecadeAndSingleYear() {
    final String dateString = "2XXX";
    final LocalDate startDate = LocalDate.parse("2000-01-01");
    final LocalDate endDate = LocalDate.parse("2999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeCenturyAndDecadeAndSingleYear() {
    final String dateString = "-2XXX";
    final LocalDate startDate = LocalDate.parse("-2999-01-01");
    final LocalDate endDate = LocalDate.parse("-2000-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYear() {
    final String dateString = "XXXX";
    final LocalDate startDate = LocalDate.parse("0000-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYear() {
    final String dateString = "-XXXX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("-0001-12-31");

    final Iso8601Date date = parse(dateString);

    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }
  
}
