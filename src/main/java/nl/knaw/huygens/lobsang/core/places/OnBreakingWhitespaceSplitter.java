package nl.knaw.huygens.lobsang.core.places;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import nl.knaw.huygens.lobsang.api.DateRequest;

public class OnBreakingWhitespaceSplitter implements SearchTermBuilder {
  private static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.breakingWhitespace()).trimResults();

  @Override
  public Iterable<String> build(DateRequest request) {
    final String place = request.getPlaceTerms();
    return WHITESPACE_SPLITTER.split(place.toLowerCase());
  }
}
