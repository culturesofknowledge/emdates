package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import java.util.List;

import static nl.knaw.huygens.lobsang.core.places.timbuctoo.HierarchyBuilder.buildHierarchy;

public class QueryBuilder {
  public static String buildQuery(
      String dataSetId,
      String collectionName,
      List<String> hierarchyStructure,
      String dateProperties,
      int depth,
      String queryName,
      String fragmentName,
      String uriProp
  ) {
    return "query " + queryName + " ($" + uriProp + ":String!) {\n" +
        "  dataSets {\n" +
        "    " + dataSetId + " {\n" +
        "      " + collectionName + "(uri: $uri) {\n" +
        "        ..." + fragmentName + "\n" +
        buildHierarchy(hierarchyStructure, createCollectionName(dataSetId, collectionName), fragmentName, depth) +
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
