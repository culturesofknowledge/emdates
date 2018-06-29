package nl.knaw.huygens.lobsang.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import nl.knaw.huygens.lobsang.ManifestParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.jar.Manifest;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
@ExtendWith(ManifestParameterResolver.class)
class AboutEndpointTest {
  private final ResourceExtension resources;

  private AboutEndpointTest(Manifest manifest) {
    resources = ResourceExtension.builder().addResource(new AboutResource(manifest)).build();
  }

  @Test
  void aboutShowsManifestAttributesInJson() throws IOException {
    final ObjectReader reader = resources.getObjectMapper().reader();

    final JsonNode actual = reader.readTree(resources.client().target("/about").request().get(String.class));
    final JsonNode expected = reader.readTree(fixture("fixtures/about.json"));

    assertThat(actual).isEqualTo(expected);

  }
}
