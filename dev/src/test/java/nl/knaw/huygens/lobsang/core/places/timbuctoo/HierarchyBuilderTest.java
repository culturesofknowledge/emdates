package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static nl.knaw.huygens.lobsang.core.places.timbuctoo.HierarchyBuilder.buildHierarchy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

class HierarchyBuilderTest {
  @Test
  void createsAnHierarchy() {
    final ArrayList<String> hierarchyProperties = Lists.newArrayList("em_hasRelationList", "items", "em_relationTo");
    final String collectionName = "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place";
    final String datePropertiesFragmentName = "placeData";
    final int depth = 3;

    final String hierarchy = buildHierarchy(hierarchyProperties, collectionName, datePropertiesFragmentName, depth);

    assertThat(hierarchy, equalToCompressingWhiteSpace(
        "em_hasRelationList {\n" +
            "  items {\n" +
            "    em_relationTo {\n" +
            "      ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
            "        ...placeData\n" +
            "        em_hasRelationList {\n" +
            "          items {\n" +
            "            em_relationTo {\n" +
            "              ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
            "                ...placeData\n" +
            "                em_hasRelationList {\n" +
            "                  items {\n" +
            "                    em_relationTo {\n" +
            "                      ... on ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place {\n" +
            "                        ...placeData\n" +
            "                      }\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
    ));
  }
}
