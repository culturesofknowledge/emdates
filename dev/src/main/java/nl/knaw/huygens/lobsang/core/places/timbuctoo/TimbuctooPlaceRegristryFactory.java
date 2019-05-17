package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistryFactory;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;

import static nl.knaw.huygens.lobsang.core.places.timbuctoo.QueryBuilder.buildQuery;

public class TimbuctooPlaceRegristryFactory implements PlaceRegistryFactory {

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
        createQuery(dataSetId, collectionName, hierarchyStructure, datePropertiesFragment)
    );
  }

  private String createQuery(String dataSetId,
                             String collectionName,
                             List<String> hierarchyStructure,
                             String datePropertiesFragment) {
    return buildQuery(
        dataSetId,
        collectionName,
        hierarchyStructure,
        datePropertiesFragment,
        10
    );
  }
}
