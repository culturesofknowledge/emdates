package nl.knaw.huygens.lobsang;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import nl.knaw.huygens.lobsang.api.KnownCalendar;
import nl.knaw.huygens.lobsang.api.Place;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

class LobsangConfiguration extends Configuration {
  @Valid
  @NotNull
  private List<Place> places = new ArrayList<>();

  @Valid
  @NotNull
  private List<KnownCalendar> calendars = new ArrayList<>();

  @JsonProperty
  List<Place> getPlaces() {
    return places;
  }

  @JsonProperty
  List<KnownCalendar> getKnownCalendars() {
    return calendars;
  }
}
