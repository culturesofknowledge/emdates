package nl.knaw.huygens.lobsang.resources;

import nl.knaw.huygens.lobsang.api.DateToParse;
import nl.knaw.huygens.lobsang.api.ParsedDate;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.parsers.ParseException;
import nl.knaw.huygens.lobsang.core.parsers.RomanDateParser;
import nl.knaw.huygens.lobsang.core.readers.CsvReader;
import nl.knaw.huygens.lobsang.core.readers.ParseFieldNames;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.HashMap;

@Path("parse")
public class ParserResource {

  private static final Logger LOG = LoggerFactory.getLogger(ParserResource.class);

  @Path("roman")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response parse(DateToParse dateToParse) {
    try {
      YearMonthDay parsedDate = RomanDateParser.parse(dateToParse.getDate());
      return Response.ok(ParsedDate.forDate(parsedDate)).build();
    } catch (ParseException ex) {
      final var message = ex.getMessage();
      LOG.error("Could not parse date: {}", message);
      return Response.ok(ParsedDate.forError(message)).build();
    }
  }

  @Path("roman/bulk")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces("text/csv")
  public Response parseBulk(@FormDataParam("file") final InputStream inputStream, FormDataMultiPart formData) {
    if (inputStream == null) {
      throw new BadRequestException("missing form param 'file=@<some_file>'");
    }

    final ParseFieldNames fieldNames = new ParseFieldNames();
    final CsvReader reader = new CsvReader.Builder(new HashMap<>(), fieldNames).build();
    try {

      reader.parse(inputStream);
      reader.validate();

      return Response.ok().entity((StreamingOutput) output -> {
        final CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(output), CSVFormat.EXCEL);
        for (String columnName : reader.getColumnNames()) {
          csvPrinter.print(columnName);
        }

        csvPrinter.print("Result");
        csvPrinter.print("Message");

        csvPrinter.println();

        reader.read(record -> {
          // add original fields
          for (String columnName : reader.getColumnNames()) {
            csvPrinter.print(record.get(columnName));
          }

          final String dateToParse = record.get(fieldNames.getDateFieldName());
          try {
            final YearMonthDay result = RomanDateParser.parse(dateToParse);
            csvPrinter.print(result.asIso8601String());
            csvPrinter.print(String.join(", ", result.getNotes()));
          } catch (ParseException ex) {
            LOG.info("Could not parse date '{}'", dateToParse);
            csvPrinter.print(""); // empty field result
            csvPrinter.print(ex.getMessage());
          }

          csvPrinter.println();
        });

        csvPrinter.flush();
        csvPrinter.close();
      }).build();
    } catch (IOException | IllegalStateException e) {
      LOG.error("Could not parse csv", e);
      return Response.status(Response.Status.BAD_REQUEST)
      .entity("Could not parse csv: " + e.getMessage() + "\n")
      .build();
    }
  }
}
