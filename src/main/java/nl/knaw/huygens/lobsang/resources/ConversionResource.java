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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Comparator.comparing;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

@Path("convert")
@Produces(MediaType.APPLICATION_JSON)
public class ConversionResource {
  public static final int MAX_CONVERSION_LIMIT = 10;
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
    return str -> String.format("\"%s\"", str);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public DateResult convert(@NotNull DateRequest dateRequest) {
    LOG.info("dateRequest: {}", dateRequest);

    final List<Place> candidates = new ArrayList<>();

    final Map<YearMonthDay, Set<String>> results
      = places.match(termBuilder.build(dateRequest))
              .peek(candidates::add)
              .map(convertForPlace(dateRequest))
              .flatMap(Function.identity())
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

    if (candidates.size() > 1) {
      final String candidateNames = joinPlaces(candidates);
      final String format = "Multiple places matched '%s': %s. Being more specific may increase accuracy.";
      result.addHint(String.format(format, dateRequest.getPlaceTerms(), candidateNames));
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
                               @FormDataParam("file") final FormDataContentDisposition fileInfo,
                               FormDataMultiPart formData) {
    LOG.debug("inputStream: {}, body: {}, fileInfo: {}", inputStream, body, fileInfo);

    if (inputStream == null || body == null || fileInfo == null) {
      throw new BadRequestException("Missing form param 'file=@<some_file>'");
    }

    LOG.debug("mediaType: {}", body.getMediaType());
    LOG.debug("fileName: {}", fileInfo.getFileName());
    LOG.debug("formData: {}", formData.getFields().keySet());
    final Map<String, String> options = extractOptions(formData);
    LOG.debug("options: {}", options);

    final CsvReader reader = new CsvReader.Builder(options).build();

    // TODO: pass targetCalendar based on request parameters
    final DateRequestBuilder dateRequestBuilder = new DateRequestBuilder();

    final int maxConversions = clampMaxConversions(options.get("maxConversions"));

    return Response.ok()
                   .type("text/csv")
                   .entity((StreamingOutput) output -> {
                     final CSVPrinter printer = new CSVPrinter(new PrintWriter(output), CSVFormat.EXCEL);
                     reader.parse(inputStream);
                     for (String column : reader.getColumnNames()) {
                       printer.print(column);
                     }
                     for (int i = 0; i < maxConversions; i++) {
                       printer.print(String.format("Y_%d", i));
                       printer.print(String.format("M_%d", i));
                       printer.print(String.format("D_%d", i));
                     }
                     printer.println();
                     reader.read(record -> copyAndConvert(reader, dateRequestBuilder, maxConversions, record, printer));
                     printer.flush();
                     printer.close();
                   })
                   .build();
  }

  private int clampMaxConversions(@Nullable String maxConversionsParam) {
    int maxConversions = Optional.ofNullable(maxConversionsParam).map(Integer::valueOf).orElse(DEFAULT_MAX_CONVERSIONS);

    if (maxConversions < 1 || MAX_CONVERSION_LIMIT < maxConversions) {
      String msg = String.format("parameter 'maxConversions' must be 1 <= maxConversions <= %d, but got: %d",
        MAX_CONVERSION_LIMIT, maxConversions);
      LOG.warn(msg);
      throw new BadRequestException(msg);
    }

    return maxConversions;
  }

  private void copyAndConvert(CsvReader reader, DateRequestBuilder dateRequestBuilder, int maxConversions,
                              CSVRecord record, CSVPrinter printer) throws IOException {
    final DateRequest dateRequest = dateRequestBuilder.build(record);

    final int columnCount = reader.getColumnNames().size();
    for (int i = 0; i < columnCount; i++) {
      printer.print(record.get(i));
    }

    // TODO: partial duplicate, clean up
    final List<YearMonthDay> conversions = places.match(termBuilder.build(dateRequest))
                                                 .map(convertForPlace(dateRequest))
                                                 .flatMap(Function.identity())
                                                 .limit(maxConversions)
                                                 .collect(Collectors.toList());

    if (conversions.isEmpty()) {
      conversions.add(defaultConversion(dateRequest));
    }

    for (YearMonthDay ymd : conversions) {
      printer.print(ymd.getYear());
      printer.print(ymd.getMonth());
      printer.print(ymd.getDay());
    }

    int shortBy = maxConversions - conversions.size();
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
    result.addNote(String.format("Based on data for place: '%s'", place.getName()));
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
    private final String targetCalendar;

    private DateRequestBuilder() {
      this("gregorian");
    }

    private DateRequestBuilder(String targetCalendar) {
      this.targetCalendar = targetCalendar;
    }

    DateRequest build(CSVRecord record) {
      return new DateRequest(
        Integer.valueOf(record.get("Y")),
        Integer.valueOf(record.get("M")),
        Integer.valueOf(record.get("D")),
        record.get("Place"),
        targetCalendar);
    }

  }
}
