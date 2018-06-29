package nl.knaw.huygens.lobsang;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.jar.Manifest;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ManifestParameterResolver implements ParameterResolver {
  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == Manifest.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    throws ParameterResolutionException {
    final Manifest manifest = new Manifest();

    try {
      manifest.read(new ByteArrayInputStream(fixture("fixtures/MANIFEST.MF").getBytes(UTF_8)));
    } catch (IOException | IllegalArgumentException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to read manifest fixture: " + e.getMessage());
    }

    return manifest;
  }
}
