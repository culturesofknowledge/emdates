package nl.knaw.huygens.lobsang.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;

import static org.assertj.core.api.Assertions.assertThat;

class AboutResourceTest {
  private AboutResource resource;

  @BeforeEach
  void setUp() {
    resource = new AboutResource();
  }

  @Test
  void aboutReturnsManifestAttributes() {
    assertThat(resource.about()).containsExactly(new SimpleEntry<>("Manifest", "not found"));
  }
}
