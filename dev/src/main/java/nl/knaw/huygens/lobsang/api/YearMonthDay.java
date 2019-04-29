package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"year", "month", "day", "notes"})
public class YearMonthDay {
  private final int day;
  private final int month;
  private final int year;

  private Set<String> notes;

  public YearMonthDay(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  @JsonProperty
  public int getYear() {
    return year;
  }

  @JsonProperty
  public int getMonth() {
    return month;
  }

  @JsonProperty
  public int getDay() {
    return day;
  }

  @JsonProperty
  public Set<String> getNotes() {
    return notes;
  }

  public void addNote(String note) {
    if (notes == null) {
      notes = new HashSet<>();
    }

    notes.add(note);
  }

  public void addNotes(Collection<String> otherNotes) {
    otherNotes.forEach(this::addNote);
  }

  public void setNotes(Set<String> notes) {
    this.notes = new HashSet<>(notes);
  }

  public YearMonthDay inNextYear() {
    final YearMonthDay nextYear = new YearMonthDay(year + 1, month, day);
    nextYear.setNotes(notes);
    return nextYear;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("year", year)
                      .add("month", month)
                      .add("day", day)
                      .add("notes", notes)
                      .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    YearMonthDay that = (YearMonthDay) obj;
    return day == that.day &&
      month == that.month &&
      year == that.year;
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, month, year);
  }

  public MonthDay asMonthDay() {
    return MonthDay.of(getMonth(), getDay());
  }

  public String asIso8601String() {
    return LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_DATE);
  }
}
