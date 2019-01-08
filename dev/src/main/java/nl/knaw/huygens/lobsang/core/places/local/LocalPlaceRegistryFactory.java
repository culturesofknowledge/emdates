package nl.knaw.huygens.lobsang.core.places.local;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.HttpClientConfiguration;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistryFactory;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;

public class LocalPlaceRegistryFactory implements PlaceRegistryFactory {

  private final List<Place> places;

  @JsonCreator
  public LocalPlaceRegistryFactory(@JsonProperty("places") List<Place> places) {
    this.places = places;
  }

  @Override
  public PlaceRegistry createPlaceRegistry(CloseableHttpClient httpClient) {
    return new LocalPlaceRegistry(places);
  }
}
