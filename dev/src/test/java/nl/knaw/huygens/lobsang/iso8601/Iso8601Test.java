package nl.knaw.huygens.lobsang.iso8601;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class Iso8601Test {
  // level 0
  @Test
  void year() {
    final String dateString = "2019";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }


  @Test
  void yearMonth() {
    final String dateString = "2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearMonth() {
    final String dateString = "-2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("-2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("-2019-04-30");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);


    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearMonthDay() {
    final String dateString = "2019-04-19";
    final LocalDate expectedDate = LocalDate.parse(dateString);

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);


    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearMonthDayCompact() {
    final String dateString = "20190419";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);


    Iso8601Date date = builder.build();
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

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearApproximate() {
    final String dateString = "2019~";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearUncertainApproximate() {
    final String dateString = "2019%";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthUncertain() {
    final String dateString = "2019-04?";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearMonthApproximate() {
    final String dateString = "2019-04~";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearMonthUncertainApproximate() {
    final String dateString = "2019-04%";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate));
    assertThat(date.getEnd(), is(expectedEndDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthDayUncertain() {
    final String dateString = "2019-04-19?";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN));
  }

  @Test
  void yearMonthDayApproximate() {
    final String dateString = "2019-04-19~";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.APPROXIMATE));
  }

  @Test
  void yearMonthDayUncertainApproximate() {
    final String dateString = "2019-04-19%";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedDate));
    assertThat(date.getEnd(), is(expectedDate));
    assertThat(date.getUncertainty(), is(Uncertainty.UNCERTAIN_APPROXIMATE));
  }

  @Test
  void yearMonthUnspecifiedDay() {
    final String dateString = "2019-04-XX";
    final LocalDate startDate = LocalDate.parse("2019-04-01");
    final LocalDate endDate = LocalDate.parse("2019-04-30");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearMonthUnspecifiedDay() {
    final String dateString = "-2019-04-XX";
    final LocalDate startDate = LocalDate.parse("-2019-04-01");
    final LocalDate endDate = LocalDate.parse("-2019-04-30");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearUnspecifiedMonthAndDay() {
    final String dateString = "2019-XX-XX";
    final LocalDate startDate = LocalDate.parse("2019-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearUnspecifiedMonthAndDay() {
    final String dateString = "-2019-XX-XX";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYearAndMonthAndDay() {
    final String dateString = "XXXX-XX-XX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void yearUnspecifiedMonth() {
    final String dateString = "2019-XX";
    final LocalDate startDate = LocalDate.parse("2019-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYearUnspecifiedMonth() {
    final String dateString = "-2019-XX";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYearAndMonth() {
    final String dateString = "XXXX-XX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedSingleYear() {
    final String dateString = "201X";
    final LocalDate startDate = LocalDate.parse("2010-01-01");
    final LocalDate endDate = LocalDate.parse("2019-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeUnspecifiedSingleYear() {
    final String dateString = "-201X";
    final LocalDate startDate = LocalDate.parse("-2019-01-01");
    final LocalDate endDate = LocalDate.parse("-2010-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedDecadeAndSingleYear() {
    final String dateString = "20XX";
    final LocalDate startDate = LocalDate.parse("2000-01-01");
    final LocalDate endDate = LocalDate.parse("2099-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeDecadeAndSingleYear() {
    final String dateString = "-20XX";
    final LocalDate startDate = LocalDate.parse("-2099-01-01");
    final LocalDate endDate = LocalDate.parse("-2000-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedCenturyAndDecadeAndSingleYear() {
    final String dateString = "2XXX";
    final LocalDate startDate = LocalDate.parse("2000-01-01");
    final LocalDate endDate = LocalDate.parse("2999-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeCenturyAndDecadeAndSingleYear() {
    final String dateString = "-2XXX";
    final LocalDate startDate = LocalDate.parse("-2999-01-01");
    final LocalDate endDate = LocalDate.parse("-2000-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void unspecifiedYear() {
    final String dateString = "XXXX";
    final LocalDate startDate = LocalDate.parse("0000-01-01");
    final LocalDate endDate = LocalDate.parse("9999-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  @Test
  void negativeYear() {
    final String dateString = "-XXXX";
    final LocalDate startDate = LocalDate.parse("-9999-01-01");
    final LocalDate endDate = LocalDate.parse("-0001-12-31");

    final Iso8601Date.Iso8601DateBuilder builder = parse(dateString);

    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(startDate));
    assertThat(date.getEnd(), is(endDate));
    assertThat(date.getUncertainty(), is(Uncertainty.NONE));
  }

  private Iso8601Date.Iso8601DateBuilder parse(String dateString) {
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final Iso8601FormatParser.Iso8601Context iso8601 = iso8601FormatParser.iso8601();
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);

    return builder;
  }
}
