package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.MoreObjects.toStringHelper;

public class KnownCalendar {
  private String name;
  private String implementationClass;

  @JsonProperty
  public String getName() {
    return name;
  }

  @JsonProperty("class")
  public String getImplementationClass() {
    return implementationClass;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("name", name)
      .add("class", implementationClass)
      .toString();
  }
}
