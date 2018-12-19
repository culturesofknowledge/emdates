package nl.knaw.huygens.lobsang;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huygens.lobsang.api.KnownCalendar;
import nl.knaw.huygens.lobsang.core.ConversionService;
import nl.knaw.huygens.lobsang.core.ConverterRegistry;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;
import nl.knaw.huygens.lobsang.core.places.ContainsAllTermsMatcher;
import nl.knaw.huygens.lobsang.core.places.OnBreakingWhitespaceSplitter;
import nl.knaw.huygens.lobsang.core.places.PlaceMatcher;
import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.places.SearchTermBuilder;
import nl.knaw.huygens.lobsang.resources.AboutResource;
import nl.knaw.huygens.lobsang.resources.ConversionResource;
import nl.knaw.huygens.lobsang.resources.ParserResource;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;

import static java.util.jar.Attributes.Name.IMPLEMENTATION_TITLE;

public class LobsangApplication extends Application<LobsangConfiguration> {
  private static final Logger LOG = LoggerFactory.getLogger(LobsangApplication.class);

  private ConverterRegistry converterRegistry;
  private PlaceRegistry placeRegistry;

  private static Manifest findManifest(String name) throws IOException {
    Enumeration<URL> resources = Thread.currentThread().getContextClassLoader()
                                       .getResources("META-INF/MANIFEST.MF");
    while (resources.hasMoreElements()) {
      final URL manifestUrl = resources.nextElement();
      final Manifest manifest = new Manifest(manifestUrl.openStream());
      final Attributes mainAttributes = manifest.getMainAttributes();
      final String implementationTitle = mainAttributes.getValue(IMPLEMENTATION_TITLE);
      if (name.equals(implementationTitle)) {
        return manifest;
      }
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    new LobsangApplication().run(args);
  }

  @Override
  public String getName() {
    return "lobsang";
  }

  @Override
  public void initialize(Bootstrap<LobsangConfiguration> bootstrap) {
    LOG.info("initializing");
    bootstrap.addBundle(new MultiPartBundle());
    bootstrap.addBundle(new AssetsBundle("/static", "/static", "static/index.html"));
  }

  public void run(LobsangConfiguration lobsangConfiguration, Environment environment) throws IOException {
    setupLogging(environment);
    converterRegistry = lobsangConfiguration.getConverterRegistry();
    placeRegistry = lobsangConfiguration.getPlaceRegistry();
    LOG.warn("registered locations: {}", lobsangConfiguration.getPlaces());
    registerResources(environment.jersey());
  }

  private void registerResources(JerseyEnvironment jersey) throws IOException {
    jersey.register(new AboutResource(findManifest(getName())));
    jersey.register(new ConversionResource(createConversionService()));
    jersey.register(new ParserResource());
  }

  private ConversionService createConversionService() {
    return new ConversionService(converterRegistry, createPlaceMatcher(), createSearchTermBuilder());
  }

  private SearchTermBuilder createSearchTermBuilder() {
    return new OnBreakingWhitespaceSplitter();
  }

  private PlaceMatcher createPlaceMatcher() {
    return new ContainsAllTermsMatcher(placeRegistry, false);
  }

  private void setupLogging(Environment environment) {
    final String commitHash = "0xdeadbeef"; // TODO: extract build properties
    MDC.put("commit_hash", commitHash);

    environment.jersey().register(new LoggingFeature(java.util.logging.Logger.getLogger(getClass().getName()),
      Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, 1024));
  }
}
