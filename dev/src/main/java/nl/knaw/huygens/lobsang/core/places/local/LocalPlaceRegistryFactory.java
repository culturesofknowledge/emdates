package nl.knaw.huygens.lobsang.core.places.local;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistryFactory;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;

public class LocalPlaceRegistryFactory implements PlaceRegistryFactory {

  private final List<Place> places;
  private final Place defaultPlace;

  @JsonCreator
  public LocalPlaceRegistryFactory(
      @JsonProperty("places") List<Place> places,
      @JsonProperty("defaultPlace") Place defaultPlace) {
    this.places = places;
    this.defaultPlace = defaultPlace;
  }

  @Override
  public PlaceRegistry createPlaceRegistry(CloseableHttpClient httpClient) {
    return new LocalPlaceRegistry(places);
  }
}
