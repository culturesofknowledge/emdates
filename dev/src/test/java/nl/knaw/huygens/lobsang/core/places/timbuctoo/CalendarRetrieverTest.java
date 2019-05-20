package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

class CalendarRetrieverTest {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String CALS_IN_PLACE = "{\n" +
      "\t\"data\": {\n" +
      "\t\t\"dataSets\": {\n" +
      "\t\t\t\"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places\": {\n" +
      "\t\t\t\t\"em_Place\": {\n" +
      "\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\"value\": \"City of Opole\"\n" +
      "\t\t\t\t\t},\n" +
      "\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\"items\": [{\n" +
      "\t\t\t\t\t\t\t\"uri\": \"BlankNode:aa6302fa-a12f-43e8-a2a3-54751128adae-opele_ttl/node1cv2q5j5nx266\",\n" +
      "\t\t\t\t\t\t\t\"em_when\": {\n" +
      "\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"Until 1584\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_timespan\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"em_start\": null,\n" +
      "\t\t\t\t\t\t\t\t\t\"em_end\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\"value\": \"1584\"\n" +
      "\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\"oa_hasBody\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar\",\n" +
      "\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"Julian (OS)\"\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\"uri\": \"BlankNode:aa6302fa-a12f-43e8-a2a3-54751128adae-opele_ttl/node1cv2q5j5nx271\",\n" +
      "\t\t\t\t\t\t\t\"em_when\": {\n" +
      "\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"Since 23-Feb-1584\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_timespan\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"em_start\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\"value\": \"1584-02-23\"\n" +
      "\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\"em_end\": null\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\"oa_hasBody\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar\",\n" +
      "\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"Gregorian\"\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}]\n" +
      "\t\t\t\t\t},\n" +
      "\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\"items\": [{\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_Duchy\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": [{\n" +
      "\t\t\t\t\t\t\t\t\t\t\"uri\": \"BlankNode:aa6302fa-a12f-43e8-a2a3-54751128adae-opele_ttl/node1cv2q5j5nx266\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\"em_when\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Until 1582\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"em_timespan\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_start\": null,\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_end\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"1582\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\"oa_hasBody\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar" +
      "\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Julian (OS)\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\t\t\t\"uri\": \"BlankNode:aa6302fa-a12f-43e8-a2a3-54751128adae-opele_ttl/node1cv2q5j5nx271\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\"em_when\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Since 23-Feb-1582\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"em_timespan\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_start\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"1582-02-23\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_end\": null\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\"oa_hasBody\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar" +
      "\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Gregorian\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t}]\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_Raciborz_Duchy\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_County\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}]\n" +
      "\t\t\t\t\t}\n" +
      "\t\t\t\t}\n" +
      "\t\t\t}\n" +
      "\t\t}\n" +
      "\t}\n" +
      "}";
  private static final String CALS_IN_HIERARCHY = "{\n" +
      "\t\"data\": {\n" +
      "\t\t\"dataSets\": {\n" +
      "\t\t\t\"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places\": {\n" +
      "\t\t\t\t\"em_Place\": {\n" +
      "\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\"value\": \"City of Opole\"\n" +
      "\t\t\t\t\t},\n" +
      "\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t},\n" +
      "\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\"items\": [{\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_Duchy\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": [{\n" +
      "\t\t\t\t\t\t\t\t\t\t\"uri\": \"BlankNode:aa6302fa-a12f-43e8-a2a3-54751128adae-opele_ttl/node1cv2q5j5nx266\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\"em_when\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Until 1582\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"em_timespan\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_start\": null,\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_end\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"1582\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\"oa_hasBody\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar" +
      "\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Julian (OS)\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\t\t\t\"uri\": \"BlankNode:aa6302fa-a12f-43e8-a2a3-54751128adae-opele_ttl/node1cv2q5j5nx271\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\"em_when\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Since 23-Feb-1582\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"em_timespan\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_start\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"1582-02-23\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"em_end\": null\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\t\t\"oa_hasBody\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Calendar" +
      "\",\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\"rdfs_label\": {\n" +
      "\t\t\t\t\t\t\t\t\t\t\t\t\"value\": \"Gregorian\"\n" +
      "\t\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t\t\t}]\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_Raciborz_Duchy\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_County\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}]\n" +
      "\t\t\t\t\t}\n" +
      "\t\t\t\t}\n" +
      "\t\t\t}\n" +
      "\t\t}\n" +
      "\t}\n" +
      "}";
  private static final String NO_CALENDARS = "{\n" +
      "\t\"data\": {\n" +
      "\t\t\"dataSets\": {\n" +
      "\t\t\t\"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places\": {\n" +
      "\t\t\t\t\"em_Place\": {\n" +
      "\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\"value\": \"City of Opole\"\n" +
      "\t\t\t\t\t},\n" +
      "\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t},\n" +
      "\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\"items\": [{\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_Duchy\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_Raciborz_Duchy\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}, {\n" +
      "\t\t\t\t\t\t\t\"em_relationTo\": {\n" +
      "\t\t\t\t\t\t\t\t\"__typename\": \"ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places_em_Place\",\n" +
      "\t\t\t\t\t\t\t\t\"title\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"value\": \"http://id.emplaces.info/place/Opole_County\"\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasAnnotationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t},\n" +
      "\t\t\t\t\t\t\t\t\"em_hasRelationList\": {\n" +
      "\t\t\t\t\t\t\t\t\t\"items\": []\n" +
      "\t\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t\t}\n" +
      "\t\t\t\t\t\t}]\n" +
      "\t\t\t\t\t}\n" +
      "\t\t\t\t}\n" +
      "\t\t\t}\n" +
      "\t\t}\n" +
      "\t}\n" +
      "}";
  private static final String DATA_SET_ID = "ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places";
  private static final String COLLECTION_NAME = "em_Place";
  private static final List<String> HIERARCHY_STRUCTURE = newArrayList("em_hasRelationList", "items", "em_relationTo");

  @Test
  void retrievesTheCalendarPeriodsFromThePlace() throws Exception {
    JsonNode place = OBJECT_MAPPER.readTree(CALS_IN_PLACE);
    CalendarRetriever instance = new CalendarRetriever(DATA_SET_ID, COLLECTION_NAME, HIERARCHY_STRUCTURE);

    List<CalendarPeriod> calendarPeriods = instance.getCalendarPeriods(place).get("City of Opole");

    assertThat(calendarPeriods, containsInAnyOrder(
        new CalendarPeriod("Julian (OS)", null, "1584"),
        new CalendarPeriod("Gregorian", "1584-02-23", null)
    ));
  }

  @Test
  void retrievesTheCalendarsFromTheRelatedPlace() throws Exception {
    JsonNode place = OBJECT_MAPPER.readTree(CALS_IN_HIERARCHY);
    CalendarRetriever instance = new CalendarRetriever(DATA_SET_ID, COLLECTION_NAME, HIERARCHY_STRUCTURE);

    List<CalendarPeriod> calendarPeriods = instance.getCalendarPeriods(place).get("City of Opole");

    assertThat(calendarPeriods, containsInAnyOrder(
        new CalendarPeriod("Julian (OS)", null, "1582"),
        new CalendarPeriod("Gregorian", "1582-02-23", null)
    ));
  }

  @Test
  void returnsAnEmptyListWhenCalendarsAreFound() throws Exception {
    JsonNode place = OBJECT_MAPPER.readTree(NO_CALENDARS);
    CalendarRetriever instance = new CalendarRetriever(DATA_SET_ID, COLLECTION_NAME, HIERARCHY_STRUCTURE);

    List<CalendarPeriod> calendarPeriods = instance.getCalendarPeriods(place).get("City of Opole");

    assertThat(calendarPeriods, empty());
  }

}


