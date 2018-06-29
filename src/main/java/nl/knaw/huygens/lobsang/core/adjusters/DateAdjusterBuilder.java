package nl.knaw.huygens.lobsang.core.adjusters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

import java.time.MonthDay;
import java.util.function.UnaryOperator;

public class DateAdjusterBuilder {
  private static final MonthDay JANUARY_FIRST = MonthDay.of(1, 1);

  private static final String ADJUSTMENT_RATIONALE_MSG =
    "Date after %s, but before start of (Old Style) new year on %s";

  private final MonthDay startOfYear;
  private MonthDay originalDate;

  private DateAdjusterBuilder(MonthDay startOfYear) {
    this.startOfYear = startOfYear;
  }

  public static DateAdjusterBuilder withNewYearOn(MonthDay startOfYear) {
    return new DateAdjusterBuilder(startOfYear);
  }

  public DateAdjusterBuilder forOriginalDate(MonthDay originalDate) {
    this.originalDate = originalDate;
    return this;
  }

  public UnaryOperator<YearMonthDay> build() {
    if (JANUARY_FIRST.equals(startOfYear) || originalDate.compareTo(startOfYear) >= 0) {
      return UnaryOperator.identity();
    }

    return advanceToNextYear();
  }

  private UnaryOperator<YearMonthDay> advanceToNextYear() {
    return date -> {
      final YearMonthDay oneYearLater = date.inNextYear();
      oneYearLater.addNote(explain(startOfYear, date.getYear()));
      return oneYearLater;
    };
  }

  private String explain(MonthDay startOfYear, int year) {
    return String.format(ADJUSTMENT_RATIONALE_MSG, JANUARY_FIRST.atYear(year), startOfYear.atYear(year));
  }

}
