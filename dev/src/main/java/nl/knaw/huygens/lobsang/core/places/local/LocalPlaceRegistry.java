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
  public Stream<Place> searchPlacesById(String placeId) {
    if (placesByGeoNamesId.containsKey(placeId)) {
      final Place place = placesByGeoNamesId.get(placeId);
      if (place.getCalendarPeriods().isEmpty()) {
        if (place.getParent().isPresent()) {
          return searchPlacesById(place.getParent().get());
        }
      } else {
        return Stream.of(place);
      }
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
