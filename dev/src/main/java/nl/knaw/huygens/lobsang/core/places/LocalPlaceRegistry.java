package nl.knaw.huygens.lobsang.core.places;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.lobsang.api.Place;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LocalPlaceRegistry implements PlaceRegistry {
  private final Map<String, Place> placesByName = new HashMap<>();

  @JsonCreator
  public LocalPlaceRegistry(@JsonProperty("places") List<Place> places) {
    places.forEach(this::addPlace);
  }

  @Override
  public Stream<String> stream() {
    return placesByName.keySet().stream();
  }

  @Override
  public Place get(String name) {
    return placesByName.get(name);
  }

  private void addPlace(Place place) {
    placesByName.put(place.getName().toLowerCase(), place);
  }
}
