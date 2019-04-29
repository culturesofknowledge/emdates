package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

public class CalendarRetriever {

  private final String dataSetId;

  public CalendarRetriever(String dataSetId) {
    this.dataSetId = dataSetId;
  }

  public List<CalendarPeriod> getCalendarPeriods(JsonNode place) {
    ArrayNode annotations = (ArrayNode) place.get("em_hasAnnotationList").get("items");
    ArrayNode relations = (ArrayNode) place.get("em_hasRelationList").get("items");

    if (stream(annotations.spliterator(), false).anyMatch(this::isCalendarAnnotation)) {
      return stream(annotations.spliterator(), false)
          .filter(this::isCalendarAnnotation)
          .map(this::createCalendar).collect(Collectors.toList());
    } else if (stream(relations.spliterator(), false).anyMatch(this::isPlaceRelation)) {
      return stream(relations.spliterator(), false)
          .filter(this::isPlaceRelation)
          .map(relation -> relation.get("em_relationTo"))
          .flatMap(node -> getCalendarPeriods(node).stream()).collect(Collectors.toList());
    }

    return Lists.newArrayList();
  }

  private boolean isPlaceRelation(JsonNode relation) {
    return relation.has("em_relationTo") &&
        relation.get("em_relationTo").has("__typename") &&
        relation.get("em_relationTo").get("__typename").asText().equals(dataSetId + "_em_Place");
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
}
