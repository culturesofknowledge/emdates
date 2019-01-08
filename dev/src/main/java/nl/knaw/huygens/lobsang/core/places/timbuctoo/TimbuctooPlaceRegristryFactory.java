package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistryFactory;
import org.apache.http.impl.client.CloseableHttpClient;

public class TimbuctooPlaceRegristryFactory implements PlaceRegistryFactory {

  private String uri;
  private final String dataSetId;

  @JsonCreator
  public TimbuctooPlaceRegristryFactory(@JsonProperty("uri") String uri, @JsonProperty("dataSetId") String dataSetId) {
    this.uri = uri;
    this.dataSetId = dataSetId;
  }

  @Override
  public PlaceRegistry createPlaceRegistry(CloseableHttpClient httpClient) {
    return new TimbuctooPlaceRegistry(httpClient, uri, dataSetId);
  }
}
