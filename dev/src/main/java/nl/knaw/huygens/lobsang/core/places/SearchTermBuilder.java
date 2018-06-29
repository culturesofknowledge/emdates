package nl.knaw.huygens.lobsang.core.places;

import nl.knaw.huygens.lobsang.api.DateRequest;

public interface SearchTermBuilder {
  Iterable<String> build(DateRequest request);
}
