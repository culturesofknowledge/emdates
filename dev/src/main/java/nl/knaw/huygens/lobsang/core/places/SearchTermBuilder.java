package nl.knaw.huygens.lobsang.core.places;

public interface SearchTermBuilder {
  Iterable<String> build(String placeTerms);
}
