package nl.knaw.huygens.lobsang.core.places;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

public class OnBreakingWhitespaceSplitter implements SearchTermBuilder {
  private static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.breakingWhitespace()).trimResults();

  @Override
  public Iterable<String> build(String placeTerms) {
    final String place = placeTerms;
    return WHITESPACE_SPLITTER.split(place.toLowerCase());
  }
}
