package nl.knaw.huygens.lobsang.iso8601;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.UnspecifiedCenturyAndDecadeAndSingleYearContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.UnspecifiedDecadeAndSingleYearContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.UnspecifiedNegativeYearContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.UnspecifiedPositiveYearContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.UnspecifiedSingleYearContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.UnspecifiedYearAndMonthAndDayContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.UnspecifiedYearAndMonthContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearApproximateContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthApproximateContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthDayApproximateContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthDayCompactContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthDayContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthDayUncertainApproximateContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthDayUncertainContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthUncertainApproximateContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthUncertainContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearMonthUnspecifiedDayContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearUncertainApproximateContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearUncertainContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearUnspecifiedMonthAndDayContext;
import static nl.knaw.huygens.lobsang.iso8601.Iso8601FormatParser.YearUnspecifiedMonthContext;

public class Iso8601DateListener extends Iso8601FormatBaseListener {
  private final Iso8601Date.Iso8601DateBuilder dateBuilder;

  public Iso8601DateListener(Iso8601Date.Iso8601DateBuilder dateBuilder) {

    this.dateBuilder = dateBuilder;
  }

  // Level 0
  @Override
  public void enterYear(YearContext ctx) {
    createYear(ctx.Year().getText(), Uncertainty.NONE);
  }

  private String isoDateString(String year, String month, String day) {
    return String.format("%s-%s-%s", year, month, day);
  }

  @Override
  public void enterYearMonth(YearMonthContext ctx) {
    createYearMonth(ctx.YearMonth().getText(), Uncertainty.NONE);
  }

  @Override
  public void enterYearMonthDay(YearMonthDayContext ctx) {
    createYearMonthDay(ctx.YearMonthDay().getText(), Uncertainty.NONE);
  }

  @Override
  public void enterYearMonthDayCompact(YearMonthDayCompactContext ctx) {
    final String text = ctx.YearMonthDayCompact().getText();

    final LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd"));

    dateBuilder.start(date);
    dateBuilder.end(date);
  }

  // Level 1
  @Override
  public void enterYearUncertain(YearUncertainContext ctx) {
    createYear(ctx.Year().getText(), Uncertainty.UNCERTAIN);
  }

  @Override
  public void enterYearApproximate(YearApproximateContext ctx) {
    createYear(ctx.Year().getText(), Uncertainty.APPROXIMATE);
  }

  @Override
  public void enterYearUncertainApproximate(YearUncertainApproximateContext ctx) {
    createYear(ctx.Year().getText(), Uncertainty.UNCERTAIN_APPROXIMATE);
  }

  @Override
  public void enterYearMonthUncertain(YearMonthUncertainContext ctx) {
    createYearMonth(ctx.YearMonth().getText(), Uncertainty.UNCERTAIN);
  }

  @Override
  public void enterYearMonthApproximate(YearMonthApproximateContext ctx) {
    createYearMonth(ctx.YearMonth().getText(), Uncertainty.APPROXIMATE);
  }

  @Override
  public void enterYearMonthUncertainApproximate(YearMonthUncertainApproximateContext ctx) {
    createYearMonth(ctx.YearMonth().getText(), Uncertainty.UNCERTAIN_APPROXIMATE);
  }

  @Override
  public void enterYearMonthDayUncertain(YearMonthDayUncertainContext ctx) {
    createYearMonthDay(ctx.YearMonthDay().getText(), Uncertainty.UNCERTAIN);
  }

  @Override
  public void enterYearMonthDayApproximate(YearMonthDayApproximateContext ctx) {
    createYearMonthDay(ctx.YearMonthDay().getText(), Uncertainty.APPROXIMATE);
  }

  @Override
  public void enterYearMonthDayUncertainApproximate(YearMonthDayUncertainApproximateContext ctx) {
    createYearMonthDay(ctx.YearMonthDay().getText(), Uncertainty.UNCERTAIN_APPROXIMATE);
  }

  @Override
  public void enterYearMonthUnspecifiedDay(YearMonthUnspecifiedDayContext ctx) {
    createYearMonth(stripUnspecifiedPartOfDateString(ctx.YearMonthUnspecifiedDay().getText()), Uncertainty.NONE);
  }

  @Override
  public void enterYearUnspecifiedMonthAndDay(YearUnspecifiedMonthAndDayContext ctx) {
    createYear(stripUnspecifiedPartOfDateString(ctx.YearUnspecifiedMonthAndDay().getText()), Uncertainty.NONE);
  }

  @Override
  public void enterUnspecifiedYearAndMonthAndDay(UnspecifiedYearAndMonthAndDayContext ctx) {
    dateBuilder.start(LocalDate.of(-9999, 1, 1));
    dateBuilder.end(LocalDate.of(9999, 12, 31));
  }

  @Override
  public void enterYearUnspecifiedMonth(YearUnspecifiedMonthContext ctx) {
    createYear(stripUnspecifiedPartOfDateString(ctx.YearUnspecifiedMonth().getText()), Uncertainty.NONE);
  }


  @Override
  public void enterUnspecifiedYearAndMonth(UnspecifiedYearAndMonthContext ctx) {
    dateBuilder.start(LocalDate.of(-9999, 1, 1));
    dateBuilder.end(LocalDate.of(9999, 12, 31));
  }

  @Override
  public void enterUnspecifiedSingleYear(UnspecifiedSingleYearContext ctx) {
    final String year = stripUnspecifiedPartOfDateString(ctx.UnspecifiedSingleYear().getText());
    dateBuilder.start(LocalDate.of(Integer.parseInt(makeFullYearString(year, true)), 1, 1));
    dateBuilder.end(LocalDate.of(Integer.parseInt(makeFullYearString(year, false)), 12, 31));
  }

  @Override
  public void enterUnspecifiedDecadeAndSingleYear(UnspecifiedDecadeAndSingleYearContext ctx) {
    final String year = stripUnspecifiedPartOfDateString(ctx.UnspecifiedDecadeAndSingleYear().getText());
    dateBuilder.start(LocalDate.of(Integer.parseInt(makeFullYearString(year, true)), 1, 1));
    dateBuilder.end(LocalDate.of(Integer.parseInt(makeFullYearString(year, false)), 12, 31));
  }

  @Override
  public void enterUnspecifiedCenturyAndDecadeAndSingleYear(UnspecifiedCenturyAndDecadeAndSingleYearContext ctx) {
    final String year = stripUnspecifiedPartOfDateString(ctx.UnspecifiedCenturyAndDecadeAndSingleYear().getText());
    dateBuilder.start(LocalDate.of(Integer.parseInt(makeFullYearString(year, true)), 1, 1));
    dateBuilder.end(LocalDate.of(Integer.parseInt(makeFullYearString(year, false)), 12, 31));
  }

  @Override
  public void enterUnspecifiedPositiveYear(UnspecifiedPositiveYearContext ctx) {
    dateBuilder.start(LocalDate.of(0, 1, 1));
    dateBuilder.end(LocalDate.of(9999, 12, 31));
  }

  @Override
  public void enterUnspecifiedNegativeYear(UnspecifiedNegativeYearContext ctx) {
    dateBuilder.start(LocalDate.of(-9999, 1, 1));
    dateBuilder.end(LocalDate.of(-1, 12, 31));
  }

  private void createYear(String dateString, Uncertainty uncertainty) {
    dateBuilder.start(LocalDate.parse(isoDateString(dateString, "01", "01")));
    dateBuilder.end(LocalDate.parse(isoDateString(dateString, "12", "31")));
    dateBuilder.uncertainty(uncertainty);
  }

  private void createYearMonth(String dateString, Uncertainty uncertainty) {
    final LocalDate startDate = LocalDate.parse(dateString + "-01");
    final LocalDate endDate = LocalDate.of(startDate.getYear(), startDate.getMonth(),
        (int) startDate.range(ChronoField.DAY_OF_MONTH).getMaximum());

    dateBuilder.start(startDate);
    dateBuilder.end(endDate);
    dateBuilder.uncertainty(uncertainty);
  }

  private void createYearMonthDay(String dateString, Uncertainty uncertainty) {
    final LocalDate date = LocalDate.parse(dateString);
    dateBuilder.start(date);
    dateBuilder.end(date);
    dateBuilder.uncertainty(uncertainty);
  }

  private String stripUnspecifiedPartOfDateString(String text) {
    final String removeUnspecified = text.substring(0, text.indexOf('X'));
    // remove the trailing dash that exists when days or months are unspecified
    return StringUtils.removeEnd(removeUnspecified, "-");
  }

  private String makeFullYearString(String year, boolean isStart) {
    int yearLength = year.startsWith("-") ? 5 : 4;
    String padding;
    if ((isStart && !year.startsWith("-")) || (!isStart && year.startsWith("-"))) {
      padding = "0";
    } else {
      padding = "9";
    }
    return StringUtils.rightPad(year, yearLength, padding);
  }
}
