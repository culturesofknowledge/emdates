package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistryFactory;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.json.Json;
import java.util.List;
import java.util.function.Function;

import static nl.knaw.huygens.lobsang.core.places.timbuctoo.QueryBuilder.buildQuery;

public class TimbuctooPlaceRegristryFactory implements PlaceRegistryFactory {

  private static final String QUERY_NAME = "emdates";
  private final String dataSetId;
  private final String datePropertiesFragment;
  private String uri;
  private String collectionName;
  private List<String> hierarchyStructure;

  @JsonCreator
  public TimbuctooPlaceRegristryFactory(
      @JsonProperty("uri") String uri,
      @JsonProperty("dataSetId") String dataSetId,
      @JsonProperty("collectionName") String collectionName,
      @JsonProperty("hierarchyStructure") List<String> hierarchyStructure,
      @JsonProperty("datePropertiesFragment") String datePropertiesFragment
  ) {
    this.uri = uri;
    this.dataSetId = dataSetId;
    this.collectionName = collectionName;
    this.hierarchyStructure = hierarchyStructure;
    this.datePropertiesFragment = datePropertiesFragment;
  }

  @Override
  public PlaceRegistry createPlaceRegistry(CloseableHttpClient httpClient) {
    return new TimbuctooPlaceRegistry(
        httpClient,
        uri,
        dataSetId,
        requestEntityBuilder()
    );
  }

  private Function<String, String> requestEntityBuilder() {
    return (placeTerms) -> Json.createObjectBuilder()
                               .add("query", createQuery())
                               .add("operationName", "emdates")
                               .add("variables", Json.createObjectBuilder().add("uri", placeTerms)).build().toString();
  }

  private String createQuery() {
    return buildQuery(
        dataSetId,
        collectionName,
        hierarchyStructure,
        datePropertiesFragment,
        10,
        QUERY_NAME
    );
  }
}
