package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class TimbuctooPlaceRegistry implements PlaceRegistry {
  private static final Logger LOG = LoggerFactory.getLogger(TimbuctooPlaceRegistry.class);
  private final CloseableHttpClient httpClient;
  private final String uri;
  private final CalendarRetriever calendarRetriever;
  private final Function<String, String> requestEntityBuilder;

  TimbuctooPlaceRegistry(CloseableHttpClient httpClient, String uri,
                         Function<String, String> requestEntityBuilder, CalendarRetriever calendarRetriever) {
    this.httpClient = httpClient;
    this.uri = uri;
    this.calendarRetriever = calendarRetriever;
    this.requestEntityBuilder = requestEntityBuilder;
  }

  @Override // TODO add exception handling / Provenance
  public Optional<Place> searchPlaceById(String placeId) {
    String requestEntity = requestEntityBuilder.apply(placeId);

    HttpPost request = new HttpPost(uri);
    request.setEntity(new StringEntity(requestEntity, ContentType.APPLICATION_JSON));

    try {
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        JsonNode responseEntity = new ObjectMapper().readTree(response.getEntity().getContent());

        return calendarRetriever.getCalendarPeriods(responseEntity).entrySet().stream()
                                .map(entry -> new Place(entry.getKey(), null, null, entry.getValue(),
                                    Lists.newArrayList()))
                                .findFirst();

      }
    } catch (IOException e) {
      LOG.error("Could not retrieve places", e);
    }

    return Optional.empty();
  }

  @Override
  public Stream<Place> allPlaces() {
    throw new UnsupportedOperationException("Not yet implemented");//FIXME: implement
  }

}
