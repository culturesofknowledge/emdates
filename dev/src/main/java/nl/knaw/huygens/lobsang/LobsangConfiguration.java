package nl.knaw.huygens.lobsang;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import nl.knaw.huygens.lobsang.core.ConverterRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

class LobsangConfiguration extends Configuration {
  @Valid
  @NotNull
  private PlaceRegistry placeRegistry;

  @Valid
  @NotNull
  private ConverterRegistry converterRegistry;

  @JsonProperty
  PlaceRegistry getPlaceRegistry() {
    return placeRegistry;
  }

  @JsonProperty
  ConverterRegistry getConverterRegistry() {
    return converterRegistry;
  }
}
