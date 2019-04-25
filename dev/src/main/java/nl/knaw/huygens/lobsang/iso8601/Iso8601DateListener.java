package nl.knaw.huygens.lobsang.iso8601;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public class Iso8601DateListener extends Iso8601FormatBaseListener {
  private final Iso8601Date.Iso8601DateBuilder dateBuilder;

  public Iso8601DateListener(Iso8601Date.Iso8601DateBuilder dateBuilder) {

    this.dateBuilder = dateBuilder;
  }

  @Override
  public void enterYear(Iso8601FormatParser.YearContext ctx) {
    final String text = ctx.Year().getText();
    dateBuilder.start(LocalDate.parse(isoDateString(text, "01", "01")));
    dateBuilder.end(LocalDate.parse(isoDateString(text, "12", "31")));
  }

  private String isoDateString(String year, String month, String day) {
    return String.format("%s-%s-%s", year, month, day);
  }

  @Override
  public void enterYearMonth(Iso8601FormatParser.YearMonthContext ctx) {
    String text = ctx.getText();
    final LocalDate startDate = LocalDate.parse(text + "-01");
    final LocalDate endDate = LocalDate.of(startDate.getYear(), startDate.getMonth(),
        (int) startDate.range(ChronoField.DAY_OF_MONTH).getMaximum());

    dateBuilder.start(startDate);
    dateBuilder.end(endDate);
  }

  @Override
  public void enterYearMonthDay(Iso8601FormatParser.YearMonthDayContext ctx) {
    final String text = ctx.YearMonthDay().getText();
    final LocalDate date = LocalDate.parse(text);
    dateBuilder.start(date);
    dateBuilder.end(date);
  }

  @Override
  public void enterYearMonthDayCompact(Iso8601FormatParser.YearMonthDayCompactContext ctx) {
    final String text = ctx.YearMonthDayCompact().getText();

    final LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd"));

    dateBuilder.start(date);
    dateBuilder.end(date);
  }
}
