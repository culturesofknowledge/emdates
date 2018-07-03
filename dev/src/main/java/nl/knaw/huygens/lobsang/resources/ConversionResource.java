package nl.knaw.huygens.lobsang.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.StartOfYear;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConversionService;
import nl.knaw.huygens.lobsang.core.adjusters.DateAdjusterBuilder;
import nl.knaw.huygens.lobsang.core.places.PlaceMatcher;
import nl.knaw.huygens.lobsang.core.places.SearchTermBuilder;
import nl.knaw.huygens.lobsang.core.readers.CsvReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

@Path("convert")
@Produces(MediaType.APPLICATION_JSON)
public class ConversionResource {
  private static final int MAX_CONVERSION_LIMIT = 10;
  private static final int DEFAULT_MAX_CONVERSIONS = 3;
  private static final Logger LOG = getLogger(ConversionResource.class);
  private final PlaceMatcher places;
  private final SearchTermBuilder termBuilder;
  private final ConversionService conversions;

  public ConversionResource(ConversionService conversions, PlaceMatcher places, SearchTermBuilder termBuilder) {
    this.places = checkNotNull(places);
    this.termBuilder = checkNotNull(termBuilder);
    this.conversions = conversions;
  }

  private static Function<String, String> asQuotedString() {
    return str -> format("\"%s\"", str);
  }

  private static <T> Stream<T> defaultIfEmpty(Stream<T> stream, Supplier<T> supplier) {
    Iterator<T> iterator = stream.iterator();
    if (iterator.hasNext()) {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    } else {
      return Stream.of(supplier.get());
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public DateResult convert(@NotNull DateRequest dateRequest) {
    LOG.info("dateRequest: {}", dateRequest);

    final List<Place> consideredPlaces = new ArrayList<>();

    final Map<YearMonthDay, Set<String>> results = convertForMatchingPlaces(dateRequest, consideredPlaces::add)
      .collect(Collectors.toMap(ymd -> ymd, YearMonthDay::getNotes, Sets::union));

    // collate notes
    results.keySet().forEach(yearMonthDay -> yearMonthDay.setNotes(results.get(yearMonthDay)));

    LOG.debug("results: {}", results);

    final DateResult result;
    if (results.isEmpty()) {
      result = new DateResult(defaultConversion(dateRequest));
      result.addHint("Requested date lies outside all defined calendar periods.");
    } else {
      LOG.debug("results (size {}): {}", results.size(), results);
      result = new DateResult(Lists.newArrayList(results.keySet()));
    }

    if (consideredPlaces.size() > 1) {
      final String names = joinPlaces(consideredPlaces);
      final String format = "Multiple places matched '%s': %s. Being more specific may increase accuracy.";
      result.addHint(format(format, dateRequest.getPlaceTerms(), names));
    }

    return result;
  }

  private YearMonthDay defaultConversion(DateRequest dateRequest) {
    return conversions.defaultConversion(asYearMonthDay(dateRequest), dateRequest.getTargetCalendar());
  }

  @POST
  @Path("table")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces("text/csv")
  public Response convertTable(@FormDataParam("file") final InputStream inputStream,
                               @FormDataParam("file") final FormDataBodyPart body,
                               FormDataMultiPart formData) {

    if (inputStream == null || body == null) {
      throw new BadRequestException("missing form param 'file=@<some_file>'");
    }

    final Map<String, String> options = extractOptions(formData);
    LOG.debug("options: {}", options);

    final int maxConversions = clampMaxConversions(options.get("maxConversions"));

    final CsvReader reader = new CsvReader.Builder(options).build();
    prepareReader(inputStream, reader);

    // TODO: pass targetCalendar based on request parameters
    final CsvReader.FieldNames fieldNames = reader.getFieldNames();
    final DateRequestBuilder dateRequestBuilder = new DateRequestBuilder(fieldNames);


    return Response.ok()
                   .type("text/csv")
                   .entity((StreamingOutput) output -> {
                     final CSVPrinter printer = new CSVPrinter(new PrintWriter(output), CSVFormat.EXCEL);
                     for (String column : reader.getColumnNames()) {
                       printer.print(column);
                     }
                     for (int i = 0; i < maxConversions; i++) {
                       printer.print(format("%s_%d", fieldNames.getYearFieldName(), i));
                       printer.print(format("%s_%d", fieldNames.getMonthFieldName(), i));
                       printer.print(format("%s_%d", fieldNames.getDayFieldName(), i));
                     }
                     printer.println();
                     reader.read(record -> {
                       copyExistingColumns(reader, record, printer);
                       convertToColumns(convertForMatchingPlaces(dateRequestBuilder.build(record)),
                         maxConversions, printer);
                     });
                     printer.flush();
                     printer.close();
                   })
                   .build();
  }

  private void prepareReader(InputStream inputStream, CsvReader reader) {
    try {
      reader.parse(inputStream);
    } catch (IOException e) {
      LOG.warn(e.getMessage());
      throw new BadRequestException(format("failed to parse CSV content: %s", e.getMessage()));
    }

    try {
      reader.validate();
    } catch (IllegalStateException e) {
      LOG.warn(e.getMessage());
      throw new BadRequestException(format("requested field names inconsistent with CSV content: %s", e.getMessage()));
    }
  }

  private int clampMaxConversions(@Nullable String maxConversionsParam) {
    int maxConversions = Optional.ofNullable(maxConversionsParam).map(Integer::valueOf).orElse(DEFAULT_MAX_CONVERSIONS);

    if (maxConversions < 1 || MAX_CONVERSION_LIMIT < maxConversions) {
      throw new BadRequestException(
        format("illegal value for parameter 'maxConversions': must be 1 <= maxConversions <= %d, but got: %d",
          MAX_CONVERSION_LIMIT, maxConversions));
    }

    return maxConversions;
  }

  private void copyExistingColumns(CsvReader reader, CSVRecord record, CSVPrinter printer) throws IOException {
    final int columnCount = reader.getColumnNames().size();
    for (int i = 0; i < columnCount; i++) {
      printer.print(record.get(i));
    }
  }

  private Stream<YearMonthDay> convertForMatchingPlaces(DateRequest dateRequest) {
    return convertForMatchingPlaces(dateRequest, null);
  }

  private Stream<YearMonthDay> convertForMatchingPlaces(DateRequest dateRequest, Consumer<Place> peepingTom) {
    Stream<Place> matchingPlaces = places.match(termBuilder.build(dateRequest));

    if (peepingTom != null) {
      matchingPlaces = matchingPlaces.peek(peepingTom);
    }

    return defaultIfEmpty(matchingPlaces.map(convertForPlace(dateRequest)).flatMap(Function.identity()),
      () -> defaultConversion(dateRequest));
  }

  private void convertToColumns(final Stream<YearMonthDay> conversions, int maxConversions, CSVPrinter printer)
    throws IOException {

    Stream<YearMonthDay> todo = conversions;

    if (maxConversions > 0) {
      LOG.trace("limiting # conversions to: {}", maxConversions);
      todo = todo.limit(maxConversions);
    }

    int shortBy = maxConversions;

    // avoid conversions.foreach() lest we end up with IOExceptions inside lambda
    for (YearMonthDay ymd : (Iterable<YearMonthDay>) todo::iterator) {
      printer.print(ymd.getYear());
      printer.print(ymd.getMonth());
      printer.print(ymd.getDay());
      shortBy--;
    }

    for (int i = 0; i < shortBy; i++) {
      // print empty fields for Y,M,D
      for (int j = 0; j < 3; j++) {
        printer.print("");
      }
    }

    // end record
    printer.println();
  }

  private Map<String, String> extractOptions(FormDataMultiPart formData) {
    return formData.getFields().entrySet().stream()
                   .filter(entry -> entry.getValue().size() > 0)
                   .filter(entry -> entry.getValue().get(0) != null)
                   .filter(entry -> MediaTypes.typeEqual(TEXT_PLAIN_TYPE, entry.getValue().get(0).getMediaType()))
                   .collect(Collectors.toMap(Map.Entry::getKey, entry -> decode(entry.getValue().get(0).getValue())));
  }

  private String decode(String value) {
    // so we can support "-F 'delimiter=%3B'" because ';' (semicolon) terminates Content-Disposition elements
    // and unfortunately some people want to use semicolons as separators in their CSV
    try {
      return URLDecoder.decode(value, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOG.warn("Unsupported encoding: {}", e.getMessage());
      e.printStackTrace();
      return value;
    }
  }

  private String joinPlaces(List<Place> places) {
    return places.stream()
                 .map(Place::getName)
                 .map(asQuotedString())
                 .sorted()
                 .collect(Collectors.joining(",", "{", "}"));
  }

  private Function<Place, Stream<YearMonthDay>> convertForPlace(DateRequest dateRequest) {
    final String targetCalendar = dateRequest.getTargetCalendar();
    final YearMonthDay requestDate = asYearMonthDay(dateRequest);
    return place -> place.getCalendarPeriods().stream()
                         .map(calendarPeriod -> conversions.convert(calendarPeriod, requestDate, targetCalendar))
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
      .map(startOfYear -> adjust(startOfYear, asMonthDay(subject), result))
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

  private MonthDay asMonthDay(YearMonthDay result) {
    return MonthDay.of(result.getMonth(), result.getDay());
  }

  private YearMonthDay asYearMonthDay(DateRequest dateRequest) {
    return new YearMonthDay(dateRequest.getYear(), dateRequest.getMonth(), dateRequest.getDay());
  }

  private class DateRequestBuilder {
    private final CsvReader.FieldNames fieldNames;
    private final String targetCalendar;

    private DateRequestBuilder(CsvReader.FieldNames fieldNames) {
      this(fieldNames, "gregorian");
    }

    private DateRequestBuilder(CsvReader.FieldNames fieldNames, String targetCalendar) {
      this.fieldNames = fieldNames;
      this.targetCalendar = targetCalendar;
    }

    DateRequest build(CSVRecord record) {
      LOG.debug("record: {}, fieldNames: {}", record, fieldNames);
      return new DateRequest(
        Integer.valueOf(record.get(fieldNames.getYearFieldName())),
        Integer.valueOf(record.get(fieldNames.getMonthFieldName())),
        Integer.valueOf(record.get(fieldNames.getDayFieldName())),
        record.get(fieldNames.getPlaceFieldName()),
        targetCalendar);
    }

  }
}
