package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import java.util.List;

class HierarchyBuilder {

  public static String buildHierarchy(List<String> hierarchyProperties, String collectionName,
                                      String calendarPropertiesFragmentName, int depth) {
    final StringBuilder query = new StringBuilder();

    buildHierarchy(hierarchyProperties, collectionName, calendarPropertiesFragmentName, depth - 1, query);

    return query.toString();
  }

  private static void buildHierarchy(List<String> hierarchyProperties, String collectionName,
                                     String datePropertiesFragmentName, int depth, StringBuilder query) {
    for (String hierarchyProperty : hierarchyProperties) {
      query.append(hierarchyProperty).append(" {\n");
    }
    query.append("... on ").append(collectionName).append(" {\n");
    query.append(datePropertiesFragmentName).append("\n");

    if (depth > 0) {
      buildHierarchy(hierarchyProperties, collectionName, datePropertiesFragmentName, depth - 1, query);
    }
    query.append("}\n");
    for (int i = 0; i < hierarchyProperties.size(); i++) {
      query.append("}\n");
    }
  }
}
