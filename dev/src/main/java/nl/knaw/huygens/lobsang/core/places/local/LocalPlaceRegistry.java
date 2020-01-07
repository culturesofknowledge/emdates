package nl.knaw.huygens.lobsang.core.places.local;

import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LocalPlaceRegistry implements PlaceRegistry {
  private final Map<String, Place> placesByGeoNamesId = new HashMap<>();

  LocalPlaceRegistry(List<Place> places) {
    places.forEach(this::addPlace);
  }

  @Override
  public Stream<Place> searchPlaces(String placeTerms) {
    if (placesByGeoNamesId.containsKey(placeTerms)) {
      return Stream.of(placesByGeoNamesId.get(placeTerms));
    }
    return Stream.empty();
  }

  @Override
  public Stream<Place> allPlaces() {
    return placesByGeoNamesId.values().stream();
  }

  private void addPlace(Place place) {
    placesByGeoNamesId.put(place.getPlaceId(), place);
  }
}
