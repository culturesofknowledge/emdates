package nl.knaw.huygens.lobsang.iso8601;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Iso8601Test {
  @Test
  public void year() {
    final String dateString = "2019";
    final LocalDate expectedStartDate = LocalDate.parse("2019-01-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-12-31");
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final Iso8601FormatParser.Iso8601Context iso8601  = iso8601FormatParser.iso8601();
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);


    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate) );
    assertThat(date.getEnd(), is(expectedEndDate) );
  }

  @Test
  public void yearMonth() {
    final String dateString = "2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("2019-04-30");
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final Iso8601FormatParser.Iso8601Context iso8601  = iso8601FormatParser.iso8601();
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);


    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate) );
    assertThat(date.getEnd(), is(expectedEndDate) );
  }

  @Test
  public void negativeYearMonth() {
    final String dateString = "-2019-04";
    final LocalDate expectedStartDate = LocalDate.parse("-2019-04-01");
    final LocalDate expectedEndDate = LocalDate.parse("-2019-04-30");
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final Iso8601FormatParser.Iso8601Context iso8601  = iso8601FormatParser.iso8601();
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);


    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedStartDate) );
    assertThat(date.getEnd(), is(expectedEndDate) );
  }

  @Test
  public void yearMonthDay() {
    final String dateString = "2019-04-19";
    final LocalDate expectedDate = LocalDate.parse(dateString);
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final Iso8601FormatParser.Iso8601Context iso8601  = iso8601FormatParser.iso8601();
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);


    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedDate) );
    assertThat(date.getEnd(), is(expectedDate) );
  }

  @Test
  public void yearMonthDayCompact() {
    final String dateString = "20190419";
    final LocalDate expectedDate = LocalDate.parse("2019-04-19");
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final Iso8601FormatParser.Iso8601Context iso8601  = iso8601FormatParser.iso8601();
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);


    Iso8601Date date = builder.build();
    assertThat(date.getStart(), is(expectedDate) );
    assertThat(date.getEnd(), is(expectedDate) );
  }
}
