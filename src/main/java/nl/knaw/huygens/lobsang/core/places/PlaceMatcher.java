package nl.knaw.huygens.lobsang.core.places;

import nl.knaw.huygens.lobsang.api.Place;

import java.util.stream.Stream;

public interface PlaceMatcher {
  Stream<Place> match(Iterable<String> searchTerms);
}
