package nl.knaw.huygens.lobsang.core.places.timbuctoo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TimbuctooPlaceDataFactory {
  private final List<String> titlePath;
  private final List<String> annotationPath;
  private final List<TimbuctooPlaceData.TimeSpan> timeSpans;
  private final List<TimbuctooPlaceData.Calendar> calendars;

  public TimbuctooPlaceDataFactory(
      @JsonProperty("titlePath") List<String> titlePath,
      @JsonProperty("annotationPath") List<String> annotationPath,
      @JsonProperty("timeSpan") List<TimbuctooPlaceData.TimeSpan> timeSpans,
      @JsonProperty("calendar") List<TimbuctooPlaceData.Calendar> calendars) {
    this.titlePath = titlePath;
    this.annotationPath = annotationPath;
    this.timeSpans = timeSpans;
    this.calendars = calendars;
  }

  public TimbuctooPlaceData newTimbuctooPlaceData(String fragmentName, String collectionType) {
    return new TimbuctooPlaceData(titlePath, annotationPath, timeSpans, calendars, fragmentName, collectionType);
  }
}
