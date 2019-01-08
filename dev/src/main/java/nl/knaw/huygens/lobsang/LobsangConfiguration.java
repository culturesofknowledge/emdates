package nl.knaw.huygens.lobsang;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import nl.knaw.huygens.lobsang.core.ConverterRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistryFactory;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

class LobsangConfiguration extends Configuration {
  @Valid
  @NotNull
  @JsonProperty("placeRegistry")
  private PlaceRegistryFactory placeRegistryFactory;

  @Valid
  @NotNull
  private ConverterRegistry converterRegistry;

  @Valid
  @NotNull
  private HttpClientConfiguration httpClient;

  @JsonProperty
  PlaceRegistry getPlaceRegistry(CloseableHttpClient httpClient) {
    return placeRegistryFactory.createPlaceRegistry(httpClient);
  }

  @JsonProperty
  ConverterRegistry getConverterRegistry() {
    return converterRegistry;
  }

  @JsonProperty
  HttpClientConfiguration getHttpClient() {
    return httpClient;
  }
}
