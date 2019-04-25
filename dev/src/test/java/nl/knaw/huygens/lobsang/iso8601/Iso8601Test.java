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
