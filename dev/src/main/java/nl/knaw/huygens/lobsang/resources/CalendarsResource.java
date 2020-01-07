package nl.knaw.huygens.lobsang.resources;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("calendars")
public class CalendarsResource {

  private final Set<String> availableCalendars;

  public CalendarsResource(Stream<String> availableCalendars) {
    this.availableCalendars = availableCalendars.map(StringUtils::capitalize).collect(Collectors.toSet());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllCalendars() {
    return Response.ok(availableCalendars).build();
  }
}
