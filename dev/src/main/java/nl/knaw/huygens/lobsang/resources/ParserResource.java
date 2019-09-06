package nl.knaw.huygens.lobsang.resources;

import nl.knaw.huygens.lobsang.api.DateToParse;
import nl.knaw.huygens.lobsang.api.ParsedDate;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.parsers.ParseException;
import nl.knaw.huygens.lobsang.core.parsers.RomanDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
}
