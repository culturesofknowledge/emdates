package nl.knaw.huygens.lobsang.core.places;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.client.HttpClientConfiguration;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface PlaceRegistryFactory {
  PlaceRegistry createPlaceRegistry(CloseableHttpClient httpClient);
}
