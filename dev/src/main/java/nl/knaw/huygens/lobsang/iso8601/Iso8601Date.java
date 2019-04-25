package nl.knaw.huygens.lobsang.iso8601;

import java.time.LocalDate;

public class Iso8601Date {
  private final LocalDate start;
  private final LocalDate end;

  private Iso8601Date(LocalDate start, LocalDate end) {
    this.start = start;
    this.end = end;
  }

  public static Iso8601DateBuilder builder() {
    return new Iso8601DateBuilder();
  }

  public LocalDate getStart() {
    return start;
  }

  public LocalDate getEnd() {
    return end;
  }

  static class Iso8601DateBuilder {
    private LocalDate start;
    private LocalDate end;

    Iso8601DateBuilder() {

    }

    public Iso8601Date build() {
      return new Iso8601Date(start, end);
    }

    void start(LocalDate start) {
      this.start = start;
    }

    void end(LocalDate end) {
      this.end = end;
    }
  }
}
