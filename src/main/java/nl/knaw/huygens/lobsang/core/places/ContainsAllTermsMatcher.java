package nl.knaw.huygens.lobsang.core.places;

import nl.knaw.huygens.lobsang.api.Place;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

public class ContainsAllTermsMatcher implements PlaceMatcher {
  private final PlaceRegistry placeRegistry;
  private final boolean streamTermsInParallel;

  public ContainsAllTermsMatcher(PlaceRegistry placeRegistry) {
    this(placeRegistry, false);
  }

  public ContainsAllTermsMatcher(PlaceRegistry placeRegistry, boolean streamTermsInParallel) {
    this.placeRegistry = checkNotNull(placeRegistry);
    this.streamTermsInParallel = streamTermsInParallel;
  }

  @Override
  public Stream<Place> match(Iterable<String> searchTerms) {
    return placeRegistry.stream().filter(matchesAll(searchTerms)).map(placeRegistry::get);
  }

  private Predicate<String> matchesAll(Iterable<String> terms) {
    return place -> streamTerms(terms).allMatch(place::contains);
  }

  private Stream<String> streamTerms(Iterable<String> terms) {
    return StreamSupport.stream(terms.spliterator(), streamTermsInParallel);
  }
}
