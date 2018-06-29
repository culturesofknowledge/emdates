package nl.knaw.huygens.lobsang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.assertj.core.api.Assertions;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Client;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;

@ExtendWith(DropwizardExtensionsSupport.class)
class LobsangApplicationIntegrationTest {

  private static final DropwizardAppExtension<LobsangConfiguration> DROPWIZARD = new DropwizardAppExtension<>(
    LobsangApplication.class, ResourceHelpers.resourceFilePath("config-test.yml")
  );

  @Test
  void runServerTest() throws IOException {
    final Client client = new JerseyClientBuilder().build();
    final ObjectReader reader = DROPWIZARD.getObjectMapper().reader();

    final JsonNode actual = reader.readTree(client.target(
      String.format("http://localhost:%d/about", DROPWIZARD.getLocalPort())).request().get(String.class));
    final JsonNode expected = reader.readTree(fixture("fixtures/empty_manifest.json"));

    Assertions.assertThat(actual).isEqualTo(expected);
  }
}
