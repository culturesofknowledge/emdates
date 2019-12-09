package nl.knaw.huygens.lobsang.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConversionService;
import nl.knaw.huygens.lobsang.core.readers.CsvReader;
import nl.knaw.huygens.lobsang.core.readers.ConvertFieldNames;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  public final ConversionService conversions;

  public ConversionResource(ConversionService conversions) {
    this.conversions = conversions;
  }

  private static Function<String, String> asQuotedString() {
    return str -> format("\"%s\"", str);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public DateResult convert(@NotNull DateRequest dateRequest) {
    LOG.info("dateRequest: {}", dateRequest);

    final List<Place> consideredPlaces = new ArrayList<>();

    final Map<YearMonthDay, Set<String>> results = conversions
        .convertForMatchingPlaces(dateRequest.getPlaceTerms(), dateRequest.asIso8601Date(),
            dateRequest.getTargetCalendar(), consideredPlaces::add
        )
        .collect(Collectors.toMap(ymd -> ymd, YearMonthDay::getNotes, Sets::union));

    // collate notes
    results.keySet().forEach(yearMonthDay -> yearMonthDay.setNotes(results.get(yearMonthDay)));

    LOG.debug("results: {}", results);

    final DateResult result;
    if (results.isEmpty()) {
      result = new DateResult(conversions.defaultConversion(
          dateRequest.asIso8601Date(),
          dateRequest.getTargetCalendar()
      ).collect(Collectors.toList()));
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

  @POST
  @Path("table")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces("text/csv")
  public Response convertTable(@FormDataParam("file") final InputStream inputStream, FormDataMultiPart formData) {
    if (inputStream == null) {
      throw new BadRequestException("missing form param 'file=@<some_file>'");
    }

    final Map<String, String> options = extractOptions(formData);
    LOG.debug("options: {}", options);

    final int maxConversions = clampMaxConversions(options.get("maxConversions"));

    final ConvertFieldNames fieldNames = ConvertFieldNames.fromConfig(options);
    final CsvReader reader = new CsvReader.Builder(options, fieldNames).build();
    prepareReader(inputStream, reader);

    // TODO: pass targetCalendar based on request parameters
    final DateRequestBuilder dateRequestBuilder = new DateRequestBuilder(fieldNames);


    return Response.ok()
                   .type("text/csv")
                   .entity((StreamingOutput) output -> {
                     final CSVPrinter printer = new CSVPrinter(new PrintWriter(output), CSVFormat.EXCEL);
                     for (String column : reader.getColumnNames()) {
                       printer.print(column);
                     }
                     for (int i = 0; i < maxConversions; i++) {
                       printer.print(format("%s_%d", fieldNames.getDateFieldName(), i));
                     }
                     printer.println();
                     reader.read(record -> {
                       copyExistingColumns(reader, record, printer);
                       convertToColumns(conversions
                               .convertForMatchingPlaces(
                                   dateRequestBuilder.build(record).getPlaceTerms(),
                                   dateRequestBuilder.build(record).asIso8601Date(),
                                   dateRequestBuilder.build(record).getTargetCalendar()),
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
      printer.print(ymd.asIso8601String());
      shortBy--;
    }

    for (int i = 0; i < shortBy; i++) {
      printer.print("");
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

  private class DateRequestBuilder {
    private final ConvertFieldNames fieldNames;
    private final String targetCalendar;

    private DateRequestBuilder(ConvertFieldNames fieldNames) {
      this(fieldNames, "gregorian");
    }

    private DateRequestBuilder(ConvertFieldNames fieldNames, String targetCalendar) {
      this.fieldNames = fieldNames;
      this.targetCalendar = targetCalendar;
    }

    DateRequest build(CSVRecord record) {
      LOG.debug("record: {}, fieldNames: {}", record, fieldNames);
      return new DateRequest(
        record.get(fieldNames.getDateFieldName()),
        record.get(fieldNames.getPlaceFieldName()),
        targetCalendar);
    }

  }
}
