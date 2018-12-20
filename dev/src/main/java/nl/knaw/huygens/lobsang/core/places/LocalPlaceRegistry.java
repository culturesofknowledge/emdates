package nl.knaw.huygens.lobsang.core.places;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.core.places.ContainsAllTermsMatcher;
import nl.knaw.huygens.lobsang.core.places.OnBreakingWhitespaceSplitter;
import nl.knaw.huygens.lobsang.core.places.PlaceMatcher;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.SearchTermBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LocalPlaceRegistry implements PlaceRegistry {
  private final Map<String, Place> placesByName = new HashMap<>();
  private final SearchTermBuilder searchTermBuilder;
  private final PlaceMatcher placeMatcher;

  @JsonCreator
  public LocalPlaceRegistry(@JsonProperty("places") List<Place> places) {
    places.forEach(this::addPlace);
    searchTermBuilder = new OnBreakingWhitespaceSplitter();
    placeMatcher = new ContainsAllTermsMatcher(placesByName.keySet());
  }

  private Place get(String name) {
    return placesByName.get(name);
  }

  @Override
  public Stream<Place> searchPlaces(String placeTerms) {
    Iterable<String> terms = searchTermBuilder.build(placeTerms);
    return placeMatcher.match(terms).map(this::get);
  }

  private void addPlace(Place place) {
    placesByName.put(place.getName().toLowerCase(), place);
  }
}
