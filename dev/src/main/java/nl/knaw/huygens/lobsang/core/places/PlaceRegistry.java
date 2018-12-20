package nl.knaw.huygens.lobsang.core.places;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.lobsang.api.Place;

import java.util.stream.Stream;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface PlaceRegistry {
  Stream<Place> searchPlaces(String placeTerms);
}
