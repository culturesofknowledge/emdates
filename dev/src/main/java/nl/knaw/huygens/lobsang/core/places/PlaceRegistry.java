package nl.knaw.huygens.lobsang.core.places;

import nl.knaw.huygens.lobsang.api.Place;

import java.util.Optional;
import java.util.stream.Stream;

public interface PlaceRegistry {
  Optional<Place> searchPlaceById(String placeId);
  Stream<Place> allPlaces();

}
