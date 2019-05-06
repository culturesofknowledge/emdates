package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import java.util.List;

import static nl.knaw.huygens.lobsang.core.places.timbuctoo.HierarchyBuilder.buildHierarchy;

public class QueryBuilder {
  public static String buildQuery(
      String dataSetId,
      String collectionName,
      List<String> hierarchyStructure,
      String dateProperties,
      int depth
      ) {
        return "query emdates ($uri:String!) {\n" +
        "  dataSets {\n" +
        "    ue85b462c027ef2b282bf87b44e9670ebb085715d__emdates_places {\n" +
        "      em_Place(uri: $uri) {\n" +
        "        ...placeData\n" +
        buildHierarchy(hierarchyStructure, createCollectionName(dataSetId, collectionName), "placeData", depth) +
        "      }\n" +
        "     }\n" +
        "   }\n" +
        " }\n" +
        dateProperties;

  }

  private static String createCollectionName(String dataSetId, String collectionName) {
    return String.format("%s_%s", dataSetId, collectionName);
  }
}
