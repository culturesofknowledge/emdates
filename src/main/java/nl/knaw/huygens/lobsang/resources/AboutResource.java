package nl.knaw.huygens.lobsang.resources;

import io.dropwizard.jersey.caching.CacheControl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;

@Path("about")
@Produces(MediaType.APPLICATION_JSON)
public class AboutResource {
  private final Map<Object, Object> info = new LinkedHashMap<>();

  public AboutResource() {
    this(null);
  }

  public AboutResource(Manifest manifest) {
    if (manifest == null) {
      info.put("Manifest", "not found");
    } else {
      manifest.getMainAttributes().forEach(info::put);
    }
  }

  @GET
  @CacheControl(maxAge = 6, maxAgeUnit = TimeUnit.HOURS)
  public Map<Object, Object> about() {
    return info;
  }

}
