package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

public class CalendarRetriever {

  private final String dataSetId;
  private final String collectionName;
  private final List<String> hierarchyStructure;

  public CalendarRetriever(String dataSetId, String collectionName, List<String> hierarchyStructure) {
    this.dataSetId = dataSetId;
    this.collectionName = collectionName;
    this.hierarchyStructure = hierarchyStructure;
  }

  public Map<String, List<CalendarPeriod>> getCalendarPeriods(JsonNode timbuctooResponse) {
    final JsonNode place = getRootPlace(timbuctooResponse);
    Map<String, List<CalendarPeriod>> placeCalendars = Maps.newHashMap();
    placeCalendars.put(getPlaceName(place), getCalendarPeriodsOfPlaceHierarchy(place));
    return placeCalendars;
  }

  private String getPlaceName(JsonNode place) {
    return place.get("title").get("value").asText();
  }


  private List<CalendarPeriod> getCalendarPeriodsOfPlaceHierarchy(JsonNode place) {
    ArrayNode annotations = (ArrayNode) place.get("em_hasAnnotationList").get("items");
    final HierarchyHelper hierarchyHelper = getRelations(place);
    ArrayNode relations = (ArrayNode) hierarchyHelper.relations;

    if (stream(annotations.spliterator(), false).anyMatch(this::isCalendarAnnotation)) {
      return stream(annotations.spliterator(), false)
          .filter(this::isCalendarAnnotation)
          .map(this::createCalendar).collect(Collectors.toList());
    } else if (stream(relations.spliterator(), false).anyMatch(
        relation -> isPlaceRelation(relation, hierarchyHelper.hierarchyStructure))
    ) {
      return stream(relations.spliterator(), false)
          .filter(relation -> isPlaceRelation(relation, hierarchyHelper.hierarchyStructure))
          .map(relation -> toPlace(relation, hierarchyHelper.hierarchyStructure))
          .flatMap(node -> getCalendarPeriodsOfPlaceHierarchy(node).stream()).collect(Collectors.toList());
    }

    return Lists.newArrayList();
  }


  private HierarchyHelper getRelations(JsonNode place) {
    JsonNode returnValue = place;

    for (Iterator<String> hierarchyIterator = hierarchyStructure.iterator(); hierarchyIterator.hasNext(); ) {
      returnValue = returnValue.get(hierarchyIterator.next());
      if (returnValue.isArray()) {
        return new HierarchyHelper(returnValue, Lists.newArrayList(hierarchyIterator));
      }
    }
    return new HierarchyHelper(returnValue, Lists.newArrayList());
  }

  private JsonNode getRootPlace(JsonNode timbuctooResponse) {
    return timbuctooResponse.get("data")
                            .get("dataSets")
                            .get("" + dataSetId + "")
                            .get(collectionName);
  }

  private JsonNode toPlace(JsonNode relation, List<String> hierarchyStructure) {
    for (String hierarchyPart : hierarchyStructure) {
      relation = relation.get(hierarchyPart);
    }

    return relation;
  }

  private boolean isPlaceRelation(JsonNode relation, List<String> hierarchyStructure) {
    for (String hierarchyPart : hierarchyStructure) {
      if (relation.has(hierarchyPart)) {
        relation = relation.get(hierarchyPart);
      }
      else {
        return false;
      }
    }
    
    return relation.has("__typename") &&
        relation.get("__typename").asText().equals(dataSetId + "_" + collectionName);
  }

  private boolean isCalendarAnnotation(JsonNode calendarAnnotation) {
    return calendarAnnotation.has("oa_hasBody") &&
        calendarAnnotation.get("oa_hasBody").has("__typename") &&
        calendarAnnotation.get("oa_hasBody").get("__typename").asText().equals(dataSetId + "_em_Calendar");
  }

  private CalendarPeriod createCalendar(JsonNode cal) {
    JsonNode timeSpan = cal.get("em_when").get("em_timespan");
    JsonNode em_start = timeSpan.get("em_start");
    String start = em_start.has("value") ? em_start.get("value").asText() : null;
    JsonNode em_end = timeSpan.get("em_end");
    String end = em_end.has("value") ? em_end.get("value").asText() : null;
    String name = cal.get("oa_hasBody").get("rdfs_label").get("value").asText();
    return new CalendarPeriod(name, start, end);
  }

  private static class HierarchyHelper {

    private final JsonNode relations;
    private final List<String> hierarchyStructure;

    public HierarchyHelper(JsonNode relations, List<String> hierarchyIterator) {
      this.relations = relations;
      this.hierarchyStructure = Lists.newArrayList(hierarchyIterator);
    }
  }
}
