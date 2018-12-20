package nl.knaw.huygens.lobsang.core.places;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

public class ContainsAllTermsMatcher implements PlaceMatcher {
  private final Set<String> placeNames;
  private final boolean streamTermsInParallel;

  public ContainsAllTermsMatcher(Set<String> placeNames) {
    this(placeNames, false);
  }

  public ContainsAllTermsMatcher(Set<String> placeNames, boolean streamTermsInParallel) {
    this.placeNames = checkNotNull(placeNames);
    this.streamTermsInParallel = streamTermsInParallel;
  }

  @Override
  public Stream<String> match(Iterable<String> searchTerms) {
    return placeNames.stream().filter(matchesAll(searchTerms));
  }

  private Predicate<String> matchesAll(Iterable<String> terms) {
    return place -> streamTerms(terms).allMatch(place::contains);
  }

  private Stream<String> streamTerms(Iterable<String> terms) {
    return StreamSupport.stream(terms.spliterator(), streamTermsInParallel);
  }
}
