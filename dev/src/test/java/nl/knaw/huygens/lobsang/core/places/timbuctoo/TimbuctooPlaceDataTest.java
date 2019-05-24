package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.google.common.collect.Lists;
import nl.knaw.huygens.lobsang.core.places.timbuctoo.TimbuctooPlaceData.Calendar;
import nl.knaw.huygens.lobsang.core.places.timbuctoo.TimbuctooPlaceData.TimeSpan;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

class TimbuctooPlaceDataTest {

  private static final String FRAGMENT_NAME = "placeData";
  private static final String COLLECTION_TYPE = "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place";
  private static final List<String> TITLE_PATH = Lists.newArrayList("title", "value");
  private static final List<String> ANNOTATION_PATH = Lists.newArrayList("em_hasAnnotationList", "items");
  private static final List<TimeSpan> TIME_SPANS = Lists.newArrayList(
      new TimeSpan(
          "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_tim_unknown",
          Lists.newArrayList("em_when", "em_timespan"),
          Lists.newArrayList("em_start", "value"),
          Lists.newArrayList("em_end", "value")
      ),
      new TimeSpan(
          "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Time_span",
          Lists.newArrayList("em_when", "em_timespan"),
          Lists.newArrayList("em_latestStart_", "value"),
          Lists.newArrayList("em_earliestEnd_", "value")
      )

  );
  private static final List<Calendar> CALENDARS = Lists.newArrayList(
      new Calendar(
          "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar",
          Lists.newArrayList("oa_hasBody"),
          Lists.newArrayList("title", "value")
      )
  );

  @Test
  void createsQueryFragment() {
    final TimbuctooPlaceData instance = new TimbuctooPlaceData(
        TITLE_PATH,
        ANNOTATION_PATH,
        TIME_SPANS,
        CALENDARS,
        FRAGMENT_NAME,
        COLLECTION_TYPE
    );

    String queryFragment = instance.queryFragment();

    assertThat(queryFragment, equalToCompressingWhiteSpace("fragment placeData on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
        "                                    title {\n" +
        "                                      value\n" +
        "                                    }\n" +
        "                                    em_hasAnnotationList {\n" +
        "                                      items {\n" +
        "                                        em_when {\n" +
        "                                          em_timespan {\n" +
        "                                            ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_tim_unknown {\n" +
        "                                              em_start {\n" +
        "                                                value\n" +
        "                                              }\n" +
        "                                              em_end {\n" +
        "                                                value\n" +
        "                                              }\n" +
        "                                            }\n" +
        "                                          }\n" +
        "                                        }\n" +
        "                                        em_when {\n" +
        "                                          em_timespan {\n" +
        "                                            ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Time_span {\n" +
        "                                              em_latestStart_ {\n" +
        "                                                value\n" +
        "                                              }\n" +
        "                                              em_earliestEnd_ {\n" +
        "                                                value\n" +
        "                                              }\n" +
        "                                            }\n" +
        "                                          }\n" +
        "                                        }\n" +
        "                                        oa_hasBody {\n" +
        "                                          ... on " +
        "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar {\n" +
        "                                            __typename" +
        "                                            title {\n" +
        "                                              value\n" +
        "                                            }\n" +
        "                                          }\n" +
        "                                        }\n" +
        "                                      }\n" +
        "                                    }\n" +
        "                                  }"
    ));

  }

}
