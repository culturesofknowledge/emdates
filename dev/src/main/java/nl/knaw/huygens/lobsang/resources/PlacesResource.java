package nl.knaw.huygens.lobsang.resources;

import nl.knaw.huygens.lobsang.core.places.PlaceRegistry;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

@Path("places")
public class PlacesResource {

  private final Set<PlaceNameAndId> allPlaces;

  public PlacesResource(PlaceRegistry placeRegistry) {
    this.allPlaces = placeRegistry.allPlaces()
                                  .map(place -> new PlaceNameAndId(place.getName(), place.getPlaceId()))
                                  .collect(Collectors.toSet());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAvailablePlaces() {
    return Response.ok(allPlaces).build();
  }

  private static class PlaceNameAndId {
    private final String name;
    private final String id;

    public PlaceNameAndId(String name, String id) {
      this.name = name;
      this.id = id;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }

      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      PlaceNameAndId that = (PlaceNameAndId) o;

      return new EqualsBuilder()
          .append(name, that.name)
          .append(id, that.id)
          .isEquals();
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder(17, 37)
          .append(name)
          .append(id)
          .toHashCode();
    }

    @Override
    public String toString() {
      return "PlaceNameAndId{" +
          "name='" + name + '\'' +
          ", id='" + id + '\'' +
          '}';
    }

    public String getName() {
      return name;
    }

    public String getId(){
      return id;
    }
  }
}
