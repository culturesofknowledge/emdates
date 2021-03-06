package nl.knaw.huygens.lobsang.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConversionService;
import nl.knaw.huygens.lobsang.core.readers.ConvertFieldNames;
import nl.knaw.huygens.lobsang.core.readers.CsvReader;
import nl.knaw.huygens.lobsang.helpers.DateStringParser;
import nl.knaw.huygens.lobsang.iso8601.Iso8601Date;
import nl.knaw.huygens.lobsang.helpers.UnsupportedDateException;
import nl.knaw.huygens.lobsang.iso8601.UnsupportedIso8601DateException;
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

import static java.lang.String.format;
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
  public Response convert(@NotNull DateRequest dateRequest) {
    LOG.info("dateRequest: {}", dateRequest);

    Iso8601Date requestDate;
    try {
      requestDate = DateStringParser.parse(dateRequest.getDate());
    } catch (UnsupportedDateException e) {

      return Response.status(Response.Status.BAD_REQUEST)
                     .entity(e.getMessage())
                     .build();
    }
    final Map<YearMonthDay, Set<String>> results = conversions
        .convertForMatchingPlaces(dateRequest.getPlaceTerms(), requestDate, dateRequest.getTargetCalendar())
        .collect(Collectors.toMap(ymd -> ymd, YearMonthDay::getNotes, Sets::union));

    // collate notes
    results.keySet().forEach(yearMonthDay -> yearMonthDay.setNotes(results.get(yearMonthDay)));

    LOG.debug("results: {}", results);

    final DateResult result;
    if (results.isEmpty()) {
      result = new DateResult(conversions.defaultConversion(
          requestDate,
          dateRequest.getTargetCalendar()
      ).collect(Collectors.toList()));
      result.addHint("Requested date lies outside all defined calendar periods.");
    } else {
      LOG.debug("results (size {}): {}", results.size(), results);
      result = new DateResult(Lists.newArrayList(results.keySet()));
    }

    return Response.ok(result).build();
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
                       printer.print(format("outputDate_%d", i));
                       printer.print(format("notes_%d", i));
                     }
                     printer.println();
                     reader.read(record -> {
                       copyExistingColumns(reader, record, printer);
                       addConversions(dateRequestBuilder.build(record), maxConversions, printer);
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

  private void addConversions(final DateRequest dateRequest, int maxConversions, CSVPrinter printer)
    throws IOException {

    int shortBy = maxConversions;

    try {
      Stream<YearMonthDay> todo = conversions.convertForMatchingPlaces(
          dateRequest.getPlaceTerms(),
          DateStringParser.parse(dateRequest.getDate()),
          dateRequest.getTargetCalendar());
      if (maxConversions > 0) {
        LOG.trace("limiting # conversions to: {}", maxConversions);
        todo = todo.distinct().limit(maxConversions);
      }

      // avoid conversions.foreach() lest we end up with IOExceptions inside lambda
      for (YearMonthDay ymd : (Iterable<YearMonthDay>) todo::iterator) {
        printer.print(ymd.asIso8601String());
        printer.print(String.join(", ", ymd.getNotes()));
        shortBy--;
      }
    } catch (UnsupportedDateException e) {
      LOG.info(e.getMessage());
      printer.print("");
      printer.print(e.getMessage());
      shortBy--;
    }

    for (int i = 0; i < shortBy; i++) {
      printer.print("");  // date
      printer.print(""); // notes
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

    private DateRequestBuilder(ConvertFieldNames fieldNames) {
      this.fieldNames = fieldNames;
    }

    DateRequest build(CSVRecord record) {
      LOG.debug("record: {}, fieldNames: {}", record, fieldNames);
      return new DateRequest(
        record.get(fieldNames.getDateFieldName()),
        record.get(fieldNames.getPlaceFieldName()),
        record.get(fieldNames.getTargetCalendarField()));
    }

  }
}
