package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.time.MonthDay;
import java.time.Year;

public class StartOfYear {
  private MonthDay when;
  private Year since;

  @JsonProperty
  public MonthDay getWhen() {
    return when;
  }

  @JsonProperty
  public Year getSince() {
    return since;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("when", when)
                      .add("since", since)
                      .toString();
  }
}
