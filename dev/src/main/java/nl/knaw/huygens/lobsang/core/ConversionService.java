package nl.knaw.huygens.lobsang.core;

import com.google.common.collect.Lists;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.StartOfYear;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.adjusters.DateAdjusterBuilder;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.iso8601.Iso8601Date;
import nl.knaw.huygens.lobsang.iso8601.Iso8601ParserHelper;
import nl.knaw.huygens.lobsang.iso8601.Uncertainty;
import nl.knaw.huygens.lobsang.iso8601.UnsupportedIso8601DateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static nl.knaw.huygens.lobsang.helpers.StreamHelpers.defaultIfEmpty;

public class ConversionService {
  public static final Logger LOG = LoggerFactory.getLogger(ConversionService.class);

  private final ConverterRegistry converters;
  private final PlaceRegistry placeRegistry;
  private final Place defaultRule;

  public ConversionService(ConverterRegistry converterRetriever, PlaceRegistry placeRegistry, Place defaultRule) {
    this.converters = converterRetriever;
    this.placeRegistry = placeRegistry;
    this.defaultRule = defaultRule;
  }

  private Optional<YearMonthDay> convert(CalendarPeriod calendarPeriod, YearMonthDay date, String calendar) {
    final Optional<CalendarConverter> reqConverter = converters.get(calendarPeriod.getCalendar());
    if (handleNonPresentConverter(reqConverter, calendarPeriod.getCalendar())) {
      return Optional.empty();
    }
    CalendarConverter requestConverter = reqConverter.get();
    final int requestDate = requestConverter.toRataDie(date);

    // Assuming this calendar is applicable, 'result' is the requestDate converted to the desired calendar
    final Optional<CalendarConverter> resultConverter = converters.get(calendar);
    if (handleNonPresentConverter(resultConverter, calendar)) {
      return Optional.empty();
    }

    final YearMonthDay result = resultConverter.get().fromRataDie(requestDate);

    // Determine if this calendar is applicable for the given date and annotate result as appropriate
    final String startDateAsString = calendarPeriod.getStartDate();
    final String endDateAsString = calendarPeriod.getEndDate();
    if (startDateAsString != null && endDateAsString != null) {
      final int startDate = requestConverter.toRataDie(calendarEarliestStart(startDateAsString));
      final int endDate = requestConverter.toRataDie(calendarLatestEnd(endDateAsString));
      if (requestDate >= startDate && requestDate <= endDate) {
        result.addNote(String.format("Date within %s calendar start and end bounds", calendarPeriod.getCalendar()));
        return Optional.of(result);
      }
    } else if (startDateAsString != null) {
      if (requestDate >= requestConverter.toRataDie(calendarEarliestStart(startDateAsString))) {
        result.addNote(String.format("Date on or after start of %s calendar", calendarPeriod.getCalendar()));
        return Optional.of(result);
      }
    } else if (endDateAsString != null) {
      if (requestDate <= requestConverter.toRataDie(calendarLatestEnd(endDateAsString))) {
        result.addNote(String.format("Date on or before end of %s calendar", calendarPeriod.getCalendar()));
        return Optional.of(result);
      }
    } else {
      result.addNote(String.format("No start or end defined for %s calendar", calendarPeriod.getCalendar()));
      return Optional.of(result);
    }

    return Optional.empty();
  }

  private boolean handleNonPresentConverter(Optional<CalendarConverter> reqConverter, String calendar) {
    if (!reqConverter.isPresent()) {
      LOG.error("Converter for calendar '{}' is not available!", calendar);
      return true;
    }
    return false;
  }

  private YearMonthDay calendarEarliestStart(String dateAsString) {
    final LocalDate date;
    try {
      date = Iso8601ParserHelper.parse(dateAsString).getStart();
    } catch (UnsupportedIso8601DateException e) {
      LOG.error("Cannot parse calendar start date '{}' exception thrown: {}", dateAsString, e.getMessage());
      throw new RuntimeException(e); // configuration error, should be impossible
    }

    return new YearMonthDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

  private YearMonthDay calendarLatestEnd(String dateAsString) {
    final LocalDate date;
    try {
      date = Iso8601ParserHelper.parse(dateAsString).getEnd();
    } catch (UnsupportedIso8601DateException e) {
      LOG.error("Cannot parse calendar end date '{}' exception thrown: {}", dateAsString, e.getMessage());
      throw new RuntimeException(e); // configuration error, should be impossible
    }

    return new YearMonthDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

  public Stream<YearMonthDay> defaultConversion(Iso8601Date date, String targetCalendar) {
    return Lists.newArrayList(date.getStartAsYearMonthDay(), date.getEndAsYearMonthDay()).stream().map(ymd -> {
      final int defaultDate = converters.defaultConverter().toRataDie(date.getStartAsYearMonthDay());
      Optional<CalendarConverter> target = converters.get(targetCalendar);
      if (handleNonPresentConverter(target, targetCalendar)) {
        throw new RuntimeException("Converter for calendar '" + targetCalendar + "' is not available!");

      }
      final YearMonthDay result = target.get().fromRataDie(defaultDate);
      result.addNote("Based on default calendar");
      return result;
    });
  }

  private Function<Place, Stream<YearMonthDay>> convertForPlace(Iso8601Date requestDate, String targetCalendar) {
    return place -> {
      if (place.getCalendarPeriods().isEmpty()) {
        return Stream.empty();
      }

      if (requestDate.getStart().equals(requestDate.getEnd())) {
        return place.getCalendarPeriods().stream()
                    .map(calendarPeriod -> convert(calendarPeriod, requestDate.getStartAsYearMonthDay(), targetCalendar))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(resultDate -> addUncertaintyNote(resultDate, requestDate))
                    .map(resultDate -> addPlaceNameNote(resultDate, place))
                    .map(resultDate -> adjustForNewYearsDay(
                        resultDate,
                        requestDate.getStartAsYearMonthDay(),
                        place.getStartOfYearList()
                    ));
      }
      final Stream<YearMonthDay> start = place.getCalendarPeriods().stream()
           .map(calendarPeriod -> convert(calendarPeriod, requestDate.getStartAsYearMonthDay(), targetCalendar))
           .filter(Optional::isPresent)
           .map(Optional::get)
           .map(resultDate -> addUncertaintyNote(resultDate, requestDate))
           .map(resultDate -> addPlaceNameNote(resultDate, place))
           .map(this::addStartDateNote)
           .map(resultDate -> adjustForNewYearsDay(
               resultDate,
               requestDate.getStartAsYearMonthDay(),
               place.getStartOfYearList()
           ));
      final Stream<YearMonthDay> end = place.getCalendarPeriods().stream()
         .map(calendarPeriod -> convert(calendarPeriod, requestDate.getEndAsYearMonthDay(), targetCalendar))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .map(resultDate -> addUncertaintyNote(resultDate, requestDate))
         .map(resultDate -> addPlaceNameNote(resultDate, place))
         .map(this::addEndDateNote)
         .map(resultDate -> adjustForNewYearsDay(
             resultDate,
             requestDate.getEndAsYearMonthDay(),
             place.getStartOfYearList()
         ));
      return Stream.concat(start, end).distinct();
    };
  }

  private YearMonthDay addEndDateNote(YearMonthDay yearMonthDay) {
    yearMonthDay.addNote("Date is the latest end of a period contained in the ISO date");

    return yearMonthDay;
  }

  private YearMonthDay addStartDateNote(YearMonthDay yearMonthDay) {
    yearMonthDay.addNote("Date is the earliest start of a period contained in the ISO date");

    return yearMonthDay;
  }

  private YearMonthDay addUncertaintyNote(YearMonthDay resultDate, Iso8601Date requestDate) {
    if(requestDate.getUncertainty() != Uncertainty.NONE) {
      resultDate.addNote(format("Date is '%s'.", requestDate.getUncertainty().name().toLowerCase().replace('_', ' ')));
    }

    return resultDate;
  }

  private YearMonthDay addPlaceNameNote(YearMonthDay result, Place place) {
    result.addNote(format("Based on data for: \"%s\"", place.getName()));
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

  public Stream<YearMonthDay> convertForMatchingPlaces(String placeTerms, Iso8601Date requestDate,
                                                       String targetCalendar) {
    Optional<Place> placeOptional = placeRegistry.searchPlaceById(placeTerms);

    return placeOptional.map(convertForPlace(requestDate, targetCalendar)::apply)
                        .orElse(convertForPlace(requestDate, targetCalendar).apply(defaultRule));
  }
}
