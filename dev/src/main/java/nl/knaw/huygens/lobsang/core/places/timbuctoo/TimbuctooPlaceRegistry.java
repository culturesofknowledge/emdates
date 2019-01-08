package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class TimbuctooPlaceRegistry implements PlaceRegistry {
  private static final Logger LOG = LoggerFactory.getLogger(TimbuctooPlaceRegistry.class);
  private final CloseableHttpClient httpClient;
  private final String uri;
  private final String query;
  private final String dataSetId;
  private final CalendarRetriever calendarRetriever;

  TimbuctooPlaceRegistry(CloseableHttpClient httpClient, String uri, String dataSetId) {
    this.httpClient = httpClient;
    this.uri = uri;
    this.query = createQuery(dataSetId);
    this.dataSetId = dataSetId;
    this.calendarRetriever = new CalendarRetriever(dataSetId);
  }

  @Override // TODO add exception handling
  public Stream<Place> searchPlaces(String placeTerms) {
    String requestEntity = Json.createObjectBuilder().add("query", query)
                               .add("operationName", "emdates")
                               .add("variables", Json.createObjectBuilder().add("uri", placeTerms)).build().toString();

    HttpPost request = new HttpPost(uri);
    request.setEntity(new StringEntity(requestEntity, ContentType.APPLICATION_JSON));

    try {
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        JsonNode responseEntity = new ObjectMapper().readTree(response.getEntity().getContent());

        JsonNode place = responseEntity.get("data")
                                       .get("dataSets")
                                       .get("" + dataSetId + "")
                                       .get("em_Place");
        List<CalendarPeriod> calendars = calendarRetriever.getCalendarPeriods(place);

        String placeName = place.get("title").get("value").asText();
        return Stream.of(new Place(placeName, calendars, Lists.newArrayList()));

      }
    } catch (IOException e) {
      LOG.error("Could not retrieve places", e);
    }

    return Stream.empty();
  }

  private String createQuery(String dataSetId) {
    return "query emdates ($uri:String!) {\n" +
        "  dataSets {\n" +
        "    ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places {\n" +
        "      em_Place(uri: $uri) {\n" +
        "        ...placeData\n" +
        "        em_hasRelationList {\n" +
        "          items {\n" +
        "            em_relationType {\n" +
        "              title {\n" +
        "                value\n" +
        "              }\n" +
        "              \n" +
        "            }\n" +
        "            em_relationTo {\n" +
        "              ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                ...placeData\n" +
        "                em_hasRelationList {\n" +
        "                  items {\n" +
        "                    em_relationTo {\n" +
        "                      ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                        ...placeData\n" +
        "                        em_hasRelationList {\n" +
        "                          items {\n" +
        "                            em_relationTo {\n" +
        "                              ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                ...placeData\n" +
        "                                em_hasRelationList {\n" +
        "                                  items {\n" +
        "                                    em_relationTo {\n" +
        "                                      ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                        ...placeData\n" +
        "                                        em_hasRelationList {\n" +
        "                                          items {\n" +
        "                                            \n" +
        "                                            em_relationTo {\n" +
        "                                              ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                                ...placeData\n" +
        "                                                em_hasRelationList {\n" +
        "                                                  items {\n" +
        "                                                    em_relationTo {\n" +
        "                                                      ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                                        ...placeData\n" +
        "                                                        em_hasRelationList {\n" +
        "                                                          items {\n" +
        "                                                            em_relationTo {\n" +
        "                                                              ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                                                ...placeData\n" +
        "                                                                em_hasRelationList {\n" +
        "                                                                  items {\n" +
        "                                                                    em_relationTo {\n" +
        "                                                                      ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                                                        ...placeData\n" +
        "                                                                        em_hasRelationList {\n" +
        "                                                                          items {\n" +
        "                                                                            em_relationTo {\n" +
        "                                                                              ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                                                                ...placeData\n" +
        "                                                                                em_hasRelationList {\n" +
        "                                                                                  items {\n" +
        "                                                                                    em_relationTo {\n" +
        "                                                                                      ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                                                                        ...placeData\n" +
        "                                                                                        em_hasRelationList " +
        "{\n" +
        "                                                                                          items {\n" +
        "                                                                                            em_relationTo " +
        "{\n" +
        "                                                                                              ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                                                                                .." +
        ".placeData\n" +
        "                                                                                              }\n" +
        "                                                                                            }\n" +
        "                                                                                          }\n" +
        "                                                                                        }\n" +
        "                                                                                      }\n" +
        "                                                                                    }\n" +
        "                                                                                  }\n" +
        "                                                                                }\n" +
        "                                                                              }\n" +
        "                                                                            }\n" +
        "                                                                          }\n" +
        "                                                                        }\n" +
        "                                                                      }\n" +
        "                                                                    }\n" +
        "                                                                  }\n" +
        "                                                                }\n" +
        "                                                              }\n" +
        "                                                            }\n" +
        "                                                          }\n" +
        "                                                        }\n" +
        "                                                      }\n" +
        "                                                    }\n" +
        "                                                  }\n" +
        "                                                }\n" +
        "                                              }\n" +
        "                                            }\n" +
        "                                          }\n" +
        "                                        }\n" +
        "                                      }\n" +
        "                                    }\n" +
        "                                  }\n" +
        "                                }\n" +
        "                              }\n" +
        "                            }\n" +
        "                          }\n" +
        "                        }\n" +
        "                      }\n" +
        "                    }\n" +
        "                  }\n" +
        "                }\n" +
        "              }\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}\n" +
        "\n" +
        "fragment placeData on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "  title {\n" +
        "    value\n" +
        "  }\n" +
        "  __typename\n" +
        "  em_hasAnnotationList {\n" +
        "    items {\n" +
        "      uri\n" +
        "      em_when {\n" +
        "        rdfs_label {\n" +
        "          value\n" +
        "        }\n" +
        "        em_timespan {\n" +
        "          ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_tim_unknown {\n" +
        "            ...unknownTimespan\n" +
        "          }\n" +
        "          ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Time_span {\n" +
        "            ...timespanTimespan\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "      oa_hasBody {\n" +
        "        ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar {\n" +
        "          __typename\n" +
        "          rdfs_label {\n" +
        "            value\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}\n" +
        "\n" +
        "fragment unknownTimespan on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_tim_unknown {\n" +
        "  em_start {\n" +
        "    value\n" +
        "  }\n" +
        "  em_end {\n" +
        "    value\n" +
        "  }\n" +
        "}\n" +
        "\n" +
        "fragment timespanTimespan on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Time_span {\n" +
        "  em_latestStart_ {\n" +
        "    value\n" +
        "  }\n" +
        "  em_earliestEnd_ {\n" +
        "    value\n" +
        "  }\n" +
        "}";
  }
}
