package nl.knaw.huygens.lobsang.core.places.local;

import com.google.common.collect.Lists;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;
import nl.knaw.huygens.lobsang.api.Place;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LocalPlaceRegistryTest {

  public static final String PLACE_ID = "placeId";
  public static final String PARENT_ID = "parent";

  @Test
  void returnsPlaceWithCalendarPeriods() {
    final Place place = new Place(
        "placeWithCal",
        PLACE_ID,
        null,
        Lists.newArrayList(new CalendarPeriod("Gregorian", "2000-12-11", null, "prov")),
        Lists.newArrayList()
    );
    final LocalPlaceRegistry instance = new LocalPlaceRegistry(Lists.newArrayList(place));

    assertThat(instance.searchPlaceById(PLACE_ID).isPresent(), is(true));
  }

  @Test
  void returnsParentWithCalendarPeriodsIfPlaceHasNone() {
    final Place place = new Place("place", PLACE_ID, PARENT_ID, Lists.newArrayList(), Lists.newArrayList());
    final Place parent = new Place(
        "parentWithCal",
        PARENT_ID,
        null,
        Lists.newArrayList(new CalendarPeriod("Gregorian", "2000-12-11", null, "prov")),
        Lists.newArrayList()
    );
    final LocalPlaceRegistry instance = new LocalPlaceRegistry(Lists.newArrayList(place, parent));

    final Optional<Place> returnPlace = instance.searchPlaceById(PLACE_ID);

    assertThat(returnPlace.isPresent(), is(true));
    assertThat(returnPlace.get().getPlaceId(), is(PARENT_ID));

  }

  @Test
  void returnsEmptyStreamWhenPlaceHasNoCalendarPeriods() {
    final Place place = new Place("place", PLACE_ID, PARENT_ID, Lists.newArrayList(), Lists.newArrayList());
    final Place parent = new Place("parent", PARENT_ID, null, Lists.newArrayList(), Lists.newArrayList()
    );
    final LocalPlaceRegistry instance = new LocalPlaceRegistry(Lists.newArrayList(place, parent));

    assertThat(instance.searchPlaceById(PLACE_ID).isPresent(), is(false));
  }

}
