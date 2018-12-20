package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.api.CalendarPeriod;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.StartOfYear;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.adjusters.DateAdjusterBuilder;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;
import nl.knaw.huygens.lobsang.core.places.PlaceMatcher;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.SearchTermBuilder;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static nl.knaw.huygens.lobsang.helpers.StreamHelpers.defaultIfEmpty;

public class ConversionService {
  private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final ConverterRegistry converters;
  private final PlaceRegistry placeRegistry;

  public ConversionService(ConverterRegistry converterRetriever,
                           PlaceRegistry placeRegistry) {
    this.converters = converterRetriever;
    this.placeRegistry = placeRegistry;
  }

  private Optional<YearMonthDay> convert(CalendarPeriod calendarPeriod, YearMonthDay date, String calendar) {
    final Optional<CalendarConverter> reqConverter = converters.get(calendarPeriod.getCalendar());
    if (!reqConverter.isPresent()) {
      throw new RuntimeException("Converter for '" + calendarPeriod.getCalendar() + "' is not available!");
    }
    CalendarConverter requestConverter = reqConverter.get();
    final int requestDate = requestConverter.toRataDie(date);

    // Assuming this calendar is applicable, 'result' is the requestDate converted to the desired calendar
    final Optional<CalendarConverter> resultConverter = converters.get(calendar);
    if (!resultConverter.isPresent()) {
      throw new RuntimeException("Converter for '" + calendarPeriod.getCalendar() + "' is not available!");
    }
    final YearMonthDay result = resultConverter.get().fromRataDie(requestDate);

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

  private YearMonthDay asYearMonthDay(String dateAsString) {
    final LocalDate date = LocalDate.parse(dateAsString, YYYY_MM_DD);
    return new YearMonthDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

  public YearMonthDay defaultConversion(YearMonthDay date, String targetCalendar) {
    final int defaultDate = converters.defaultConverter().toRataDie(date);
    Optional<CalendarConverter> target = converters.get(targetCalendar);
    if (!target.isPresent()) {
      throw new RuntimeException("Converter for '" + targetCalendar + "' is not available!");
    }
    final YearMonthDay result = target.get().fromRataDie(defaultDate);
    result.addNote("Based on default calendar");
    return result;
  }

  private Function<Place, Stream<YearMonthDay>> convertForPlace(YearMonthDay requestDate, String targetCalendar) {
    return place -> place.getCalendarPeriods().stream()
                         .map(calendarPeriod -> convert(calendarPeriod, requestDate, targetCalendar))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .map(resultDate -> addPlaceNameNote(resultDate, place))
                         .map(resultDate -> adjustForNewYearsDay(resultDate, requestDate, place.getStartOfYearList()));
  }

  private YearMonthDay addPlaceNameNote(YearMonthDay result, Place place) {
    result.addNote(format("Based on data for place: '%s'", place.getName()));
    return result;
  }

  private YearMonthDay adjustForNewYearsDay(YearMonthDay result, YearMonthDay subject, List<StartOfYear> startOfYears) {
    return findNewYearsDay(startOfYears, Year.of(subject.getYear()))
                                         .map(startOfYear -> adjust(startOfYear, subject.asMonthDay(), result))
                                         .orElseGet(() -> {
        result.addNote("No place-specific data about when the New Year started, assuming 1 January (no adjustments)");
        return result;
      });
  }

  private Optional<StartOfYear> findNewYearsDay(List<StartOfYear> startOfYears, Year subjectYear) {
    return startOfYears.stream()
                       .filter(startOfYear -> startOfYear.getSince().compareTo(subjectYear) <= 0)
                       .max(comparing(StartOfYear::getSince));
  }

  private YearMonthDay adjust(StartOfYear startOfYear, MonthDay originalDate, YearMonthDay result) {
    return DateAdjusterBuilder.withNewYearOn(startOfYear.getWhen()).forOriginalDate(originalDate).build()
                              .apply(result);
  }

  public Stream<YearMonthDay> convertForMatchingPlaces(String placeTerms, YearMonthDay requestDate,
                                                       String targetCalendar, Consumer<Place> peepingTom) {
    Stream<Place> matchingPlaces = placeRegistry.searchPlaces(placeTerms);

    if (peepingTom != null) {
      matchingPlaces = matchingPlaces.peek(peepingTom);
    }

    return defaultIfEmpty(matchingPlaces.map(convertForPlace(requestDate,
        targetCalendar)).flatMap(Function.identity()),
      () -> defaultConversion(requestDate, targetCalendar));
  }

  public Stream<YearMonthDay> convertForMatchingPlaces(String placeTerms, YearMonthDay requestDate,
                                                       String targetCalendar) {
    return convertForMatchingPlaces(placeTerms, requestDate, targetCalendar, null);
  }
}
