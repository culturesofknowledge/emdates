package nl.knaw.huygens.lobsang.core.places;

import java.util.stream.Stream;

public interface PlaceMatcher {
  Stream<String> match(Iterable<String> searchTerms);
}
