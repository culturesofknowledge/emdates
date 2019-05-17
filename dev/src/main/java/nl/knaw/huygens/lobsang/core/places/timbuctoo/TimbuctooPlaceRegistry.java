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

import javax.json.Json;
import java.io.IOException;
import java.util.stream.Stream;

public class TimbuctooPlaceRegistry implements PlaceRegistry {
  private static final Logger LOG = LoggerFactory.getLogger(TimbuctooPlaceRegistry.class);
  private final CloseableHttpClient httpClient;
  private final String uri;
  private final String query;
  private final String dataSetId;
  private final CalendarRetriever calendarRetriever;

  TimbuctooPlaceRegistry(CloseableHttpClient httpClient, String uri, String dataSetId, String query) {
    this.httpClient = httpClient;
    this.uri = uri;
    this.query = query;
    this.dataSetId = dataSetId;
    this.calendarRetriever = new CalendarRetriever(dataSetId);
  }

  @Override // TODO add exception handling / Provenance
  public Stream<Place> searchPlaces(String placeTerms) {
    String requestEntity = Json.createObjectBuilder().add("query", query)
                               .add("operationName", "emdates")
                               .add("variables", Json.createObjectBuilder().add("uri", placeTerms)).build().toString();

    HttpPost request = new HttpPost(uri);
    request.setEntity(new StringEntity(requestEntity, ContentType.APPLICATION_JSON));

    try {
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        JsonNode responseEntity = new ObjectMapper().readTree(response.getEntity().getContent());

        return calendarRetriever.getCalendarPeriods(responseEntity).entrySet().stream()
                                .map(entry -> new Place(entry.getKey(), entry.getValue(), Lists.newArrayList()));

      }
    } catch (IOException e) {
      LOG.error("Could not retrieve places", e);
    }

    return Stream.empty();
  }
}
