package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.api.CalendarPeriod;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ConversionService {
  private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final ConverterRegistry converters;

  public ConversionService(ConverterRegistry converters) {
    this.converters = converters;
  }

  public Optional<YearMonthDay> convert(CalendarPeriod calendarPeriod, YearMonthDay date, String calendar) {
    final CalendarConverter requestConverter = converters.get(calendarPeriod.getCalendar());
    final int requestDate = requestConverter.toRataDie(date);

    // Assuming this calendar is applicable, 'result' is the requestDate converted to the desired calendar
    final CalendarConverter resultConverter = converters.get(calendar);
    final YearMonthDay result = resultConverter.fromRataDie(requestDate);

    // Determine if this calendar is applicable for the given date and annotate result as appropriate
    final String startDateAsString = calendarPeriod.getStartDate();
    final String endDateAsString = calendarPeriod.getEndDate();
    if (startDateAsString != null && endDateAsString != null) {
      final int startDate = requestConverter.toRataDie(asYearMonthDay(startDateAsString));
      final int endDate = requestConverter.toRataDie(asYearMonthDay(endDateAsString));
      if (requestDate >= startDate && requestDate <= endDate) {
        result.addNote(String.format("Date within %s calendar start and end bounds", calendarPeriod.getCalendar()));
        return Optional.of(result);
      }
    } else if (startDateAsString != null) {
      if (requestDate >= requestConverter.toRataDie(asYearMonthDay(startDateAsString))) {
        result.addNote(String.format("Date on or after start of %s calendar", calendarPeriod.getCalendar()));
        return Optional.of(result);
      }
    } else if (endDateAsString != null) {
      if (requestDate <= requestConverter.toRataDie(asYearMonthDay(endDateAsString))) {
        result.addNote(String.format("Date on or before end of %s calendar", calendarPeriod.getCalendar()));
        return Optional.of(result);
      }
    } else {
      result.addNote(String.format("No start or end defined for %s calendar", calendarPeriod.getCalendar()));
      return Optional.of(result);
    }

    return Optional.empty();
  }

  public YearMonthDay defaultConversion(YearMonthDay date, String targetCalendar) {
    final int defaultDate = converters.defaultConverter().toRataDie(date);
    final YearMonthDay result = converters.get(targetCalendar).fromRataDie(defaultDate);
    result.addNote("Based on default calendar");
    return result;
  }

  private YearMonthDay asYearMonthDay(String dateAsString) {
    final LocalDate date = LocalDate.parse(dateAsString, YYYY_MM_DD);
    return new YearMonthDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

}
