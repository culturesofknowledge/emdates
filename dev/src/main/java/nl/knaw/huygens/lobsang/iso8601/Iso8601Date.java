package nl.knaw.huygens.lobsang.iso8601;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

import java.time.LocalDate;

public class Iso8601Date {
  private final LocalDate start;
  private final LocalDate end;
  private Uncertainty uncertainty;

  private Iso8601Date(LocalDate start, LocalDate end, Uncertainty uncertainty) {
    this.start = start;
    this.end = end;
    this.uncertainty = uncertainty;
  }

  public static Iso8601DateBuilder builder() {
    return new Iso8601DateBuilder();
  }

  public static Iso8601Date fromYearMonthDay(YearMonthDay ymd) {
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final LocalDate localDate = LocalDate.of(ymd.getYear(), ymd.getMonth(), ymd.getDay());
    builder.start(localDate);
    builder.end(localDate);
    return builder.build();
  }

  public LocalDate getStart() {
    return start;
  }

  public YearMonthDay getStartAsYearMonthDay() {
    return YearMonthDay.fromLocalDate(start);
  }

  public LocalDate getEnd() {
    return end;
  }

  public YearMonthDay getEndAsYearMonthDay() {
    return YearMonthDay.fromLocalDate(end);
  }

  public Uncertainty getUncertainty() {
    return uncertainty;
  }

  static class Iso8601DateBuilder {
    private Uncertainty uncertainty;
    private LocalDate start;
    private LocalDate end;

    Iso8601DateBuilder() {
      uncertainty = Uncertainty.NONE;
    }

    public Iso8601Date build() {
      return new Iso8601Date(start, end, uncertainty);
    }

    void start(LocalDate start) {
      this.start = start;
    }

    void end(LocalDate end) {
      this.end = end;
    }

    void uncertainty(Uncertainty uncertainty) {
      this.uncertainty = uncertainty;
    }
  }
}
