package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.assertj.core.util.Lists;

import java.util.List;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"dates", "hints"})
public class DateResult {
  private final List<YearMonthDay> dates;

  private List<String> hints;

  public DateResult(YearMonthDay date) {
    this(Lists.newArrayList(date));
  }

  public DateResult(List<YearMonthDay> dates) {
    this.dates = dates;
  }

  @JsonProperty
  public List<YearMonthDay> getDates() {
    return dates;
  }

  @JsonProperty
  public List<String> getHints() {
    return hints;
  }

  public void addHint(String hint) {
    if (hints == null) {
      hints = Lists.newArrayList(hint);
    } else {
      hints.add(hint);
    }
  }
}
