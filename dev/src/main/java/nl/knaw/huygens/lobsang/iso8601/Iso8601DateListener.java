package nl.knaw.huygens.lobsang.iso8601;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public class Iso8601DateListener extends Iso8601FormatBaseListener {
  private final Iso8601Date.Iso8601DateBuilder dateBuilder;

  public Iso8601DateListener(Iso8601Date.Iso8601DateBuilder dateBuilder) {

    this.dateBuilder = dateBuilder;
  }

  // Level 0
  @Override
  public void enterYear(Iso8601FormatParser.YearContext ctx) {
    createYear(ctx.Year(), Uncertainty.NONE);
  }

  private String isoDateString(String year, String month, String day) {
    return String.format("%s-%s-%s", year, month, day);
  }

  @Override
  public void enterYearMonth(Iso8601FormatParser.YearMonthContext ctx) {
    createYearMonth(ctx.YearMonth(), Uncertainty.NONE);
  }

  @Override
  public void enterYearMonthDay(Iso8601FormatParser.YearMonthDayContext ctx) {
    createYearMonthDay(ctx.YearMonthDay(), Uncertainty.NONE);
  }

  @Override
  public void enterYearMonthDayCompact(Iso8601FormatParser.YearMonthDayCompactContext ctx) {
    final String text = ctx.YearMonthDayCompact().getText();

    final LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd"));

    dateBuilder.start(date);
    dateBuilder.end(date);
  }

  // Level 1
  @Override
  public void enterYearUncertain(Iso8601FormatParser.YearUncertainContext ctx) {
    createYear(ctx.Year(), Uncertainty.UNCERTAIN);
  }

  @Override
  public void enterYearApproximate(Iso8601FormatParser.YearApproximateContext ctx) {
    createYear(ctx.Year(), Uncertainty.APPROXIMATE);
  }

  @Override
  public void enterYearUncertainApproximate(Iso8601FormatParser.YearUncertainApproximateContext ctx) {
    createYear(ctx.Year(), Uncertainty.UNCERTAIN_APPROXIMATE);
  }

  @Override
  public void enterYearMonthUncertain(Iso8601FormatParser.YearMonthUncertainContext ctx) {
    createYearMonth(ctx.YearMonth(), Uncertainty.UNCERTAIN);
  }

  @Override
  public void enterYearMonthApproximate(Iso8601FormatParser.YearMonthApproximateContext ctx) {
    createYearMonth(ctx.YearMonth(), Uncertainty.APPROXIMATE);
  }

  @Override
  public void enterYearMonthUncertainApproximate(Iso8601FormatParser.YearMonthUncertainApproximateContext ctx) {
    createYearMonth(ctx.YearMonth(), Uncertainty.UNCERTAIN_APPROXIMATE);
  }

  @Override
  public void enterYearMonthDayUncertain(Iso8601FormatParser.YearMonthDayUncertainContext ctx) {
    createYearMonthDay(ctx.YearMonthDay(), Uncertainty.UNCERTAIN);
  }

  @Override
  public void enterYearMonthDayApproximate(Iso8601FormatParser.YearMonthDayApproximateContext ctx) {
    createYearMonthDay(ctx.YearMonthDay(), Uncertainty.APPROXIMATE);
  }

  @Override
  public void enterYearMonthDayUncertainApproximate(Iso8601FormatParser.YearMonthDayUncertainApproximateContext ctx) {
    createYearMonthDay(ctx.YearMonthDay(), Uncertainty.UNCERTAIN_APPROXIMATE);
  }

  private void createYear(TerminalNode year, Uncertainty uncertainty) {
    final String text = year.getText();
    dateBuilder.start(LocalDate.parse(isoDateString(text, "01", "01")));
    dateBuilder.end(LocalDate.parse(isoDateString(text, "12", "31")));
    dateBuilder.uncertainty(uncertainty);
  }

  private void createYearMonth(TerminalNode terminalNode, Uncertainty uncertainty) {
    String text = terminalNode.getText();
    final LocalDate startDate = LocalDate.parse(text + "-01");
    final LocalDate endDate = LocalDate.of(startDate.getYear(), startDate.getMonth(),
        (int) startDate.range(ChronoField.DAY_OF_MONTH).getMaximum());

    dateBuilder.start(startDate);
    dateBuilder.end(endDate);
    dateBuilder.uncertainty(uncertainty);
  }

  private void createYearMonthDay(TerminalNode terminalNode, Uncertainty uncertainty) {
    final String text = terminalNode.getText();
    final LocalDate date = LocalDate.parse(text);
    dateBuilder.start(date);
    dateBuilder.end(date);
    dateBuilder.uncertainty(uncertainty);
  }
}
