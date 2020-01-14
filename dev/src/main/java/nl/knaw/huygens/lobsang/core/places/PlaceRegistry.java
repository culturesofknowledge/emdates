package nl.knaw.huygens.lobsang.core.places;

import nl.knaw.huygens.lobsang.api.Place;

import java.util.stream.Stream;

public interface PlaceRegistry {
  Stream<Place> searchPlacesById(String placeId);
  Stream<Place> allPlaces();

}
