package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimbuctooPlaceData {
  private final List<String> titlePath;
  private final List<String> annotationPath;
  private final List<TimeSpan> timeSpans;
  private final List<Calendar> calendars;
  private final String fragmentName;
  private final String collectionType;

  public TimbuctooPlaceData(
      List<String> titlePath,
      List<String> annotationPath,
      List<TimeSpan> timeSpans,
      List<Calendar> calendars,
      String fragmentName,
      String collectionType) {
    this.titlePath = titlePath;
    this.annotationPath = annotationPath;
    this.timeSpans = timeSpans;
    this.calendars = calendars;
    this.fragmentName = fragmentName;
    this.collectionType = collectionType;
  }

  public String queryFragment() {
    final StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("fragment ").append(fragmentName).append(" on ").append(collectionType).append(" {\n");
    beginPath(stringBuilder, titlePath);
    endPath(stringBuilder, titlePath);

    beginPath(stringBuilder, annotationPath);

    for (TimeSpan timeSpan : timeSpans) {
      beginPath(stringBuilder, timeSpan.pathToTimeSpan);
      stringBuilder.append("... on ").append(timeSpan.type).append(" {\n");
      stringBuilder.append("__typename\n");
      beginPath(stringBuilder, timeSpan.pathToStartCalendar);
      endPath(stringBuilder, timeSpan.pathToStartCalendar);

      beginPath(stringBuilder, timeSpan.pathToEndCalendar);
      endPath(stringBuilder, timeSpan.pathToEndCalendar);

      stringBuilder.append("\n}\n");
      endPath(stringBuilder, timeSpan.pathToTimeSpan);
    }

    for (Calendar calendar : calendars) {
      beginPath(stringBuilder, calendar.pathToCalendar);
      stringBuilder.append("... on ").append(calendar.type).append(" {\n");
      stringBuilder.append("__typename\n");
      beginPath(stringBuilder, calendar.pathToCalendarName);
      endPath(stringBuilder, calendar.pathToCalendarName);
      stringBuilder.append("\n}\n");
      endPath(stringBuilder, calendar.pathToCalendar);
    }


    endPath(stringBuilder, annotationPath);

    stringBuilder.append("\n}");

    return stringBuilder.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
  }

  private void endPath(StringBuilder stringBuilder, List<String> path) {
    int pathSize = path.contains("value") ? path.size() - 1 : path.size();
    for (int i = 0; i < pathSize; i++) {
      stringBuilder.append("\n}\n");
    }
  }

  private void beginPath(StringBuilder stringBuilder, List<String> path) {
    for (String step : path) {
      stringBuilder.append(step);
      if (!step.equals("value")) {
        stringBuilder.append(" {\n");
      }
    }

  }

  public boolean isCalendarAnnotation(JsonNode annotation) {
    return calendars.stream().anyMatch(calendar -> {
      final List<String> path = calendar.pathToCalendar;
      JsonNode annotationStep = walkPath(annotation, path);
      return annotationStep.has("__typename") && annotationStep.get("__typename").asText().equals(calendar.type);
    });
  }

  public String getPlaceName(JsonNode place) {
    JsonNode pathStep = walkPath(place, titlePath);

    return pathStep.asText();
  }

  private JsonNode walkPath(JsonNode place, List<String> titlePath) {
    JsonNode pathStep = place;
    for (String step : titlePath) {
      if (pathStep.has(step)) {
        pathStep = pathStep.get(step);
      }
    }
    return pathStep;
  }

  public JsonNode getAnnotations(JsonNode place) {
    return walkPath(place, annotationPath);
  }

  public CalendarPeriod createCalendar(JsonNode calendarAnnotation) {
    final Optional<String> calendarName = calendars.stream()
                                                   .filter(cal -> {
                                                     final JsonNode calendarNode =
                                                         walkPath(calendarAnnotation, cal.pathToCalendar);
                                                     return calendarNode.has("__typename") &&
                                                         calendarNode.get("__typename").asText().equals(cal.type);
                                                   })
                                                   .map(cal -> {
                                                     final JsonNode calendarNode =
                                                         walkPath(calendarAnnotation, cal.pathToCalendar);
                                                     final JsonNode calName =
                                                         walkPath(calendarNode, cal.pathToCalendarName);
                                                     return calName.asText();
                                                   }).findFirst();
    final List<String> timeSpan = timeSpans
        .stream()
        .filter(span -> {
          final JsonNode timeSpanNode1 =
              walkPath(calendarAnnotation, span.pathToTimeSpan);
          return timeSpanNode1.has("__typename") &&
              timeSpanNode1.get("__typename").asText().equals(span.type);
        }).map(span -> {
          final JsonNode timeSpanNode = walkPath(calendarAnnotation, span.pathToTimeSpan);
          final JsonNode startNode = walkPath(timeSpanNode, span.pathToStartCalendar);
          final String start = startNode.isNull() ? null : startNode.asText();
          final JsonNode endNode = walkPath(timeSpanNode, span.pathToEndCalendar);
          final String end = endNode.isNull() ? null : endNode.asText();

          return Lists.newArrayList(start, end).stream();
        }).flatMap(val -> val).collect(Collectors.toList());

    return new CalendarPeriod(calendarName.get(), timeSpan.get(0), timeSpan.get(1), "");
  }

  static class TimeSpan {
    private final String type;
    private final List<String> pathToTimeSpan;
    private final List<String> pathToStartCalendar;
    private final List<String> pathToEndCalendar;

    @JsonCreator
    TimeSpan(
        @JsonProperty("type") String type,
        @JsonProperty("pathToTimeSpan") List<String> pathToTimeSpan,
        @JsonProperty("pathToCalendarStart") List<String> pathToCalendarStart,
        @JsonProperty("pathToCalendarEnd") List<String> pathToCalendarEnd
    ) {
      this.type = type;
      this.pathToTimeSpan = pathToTimeSpan;
      this.pathToStartCalendar = pathToCalendarStart;
      this.pathToEndCalendar = pathToCalendarEnd;
    }
  }

  static class Calendar {
    private final String type;
    private final List<String> pathToCalendar;
    private final List<String> pathToCalendarName;

    @JsonCreator
    Calendar(
        @JsonProperty("type") String type,
        @JsonProperty("pathToCalendar") List<String> pathToCalendar,
        @JsonProperty("pathToCalendarName") List<String> pathToCalendarName
    ) {
      this.type = type;
      this.pathToCalendar = pathToCalendar;
      this.pathToCalendarName = pathToCalendarName;
    }
  }
}
