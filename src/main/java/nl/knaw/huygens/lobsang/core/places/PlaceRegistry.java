package nl.knaw.huygens.lobsang.core.places;

import nl.knaw.huygens.lobsang.api.Place;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PlaceRegistry {
  private final Map<String, Place> placesByName = new HashMap<>();

  public PlaceRegistry(List<Place> places) {
    places.forEach(this::addPlace);
  }

  Stream<String> stream() {
    return placesByName.keySet().stream();
  }

  Place get(String name) {
    return placesByName.get(name);
  }

  private void addPlace(Place place) {
    placesByName.put(place.getName().toLowerCase(), place);
  }
}
