package nl.knaw.huygens.lobsang.concordion;

import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.parsers.ParseException;
import nl.knaw.huygens.lobsang.core.parsers.RomanDateParser;
import org.concordion.api.MultiValueResult;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class Scaliger {
  public MultiValueResult parse(String input) throws ParseException {
    final YearMonthDay parsed;
    String isoDate = "????-??-??";
    String warning = "";
    try {
      parsed = RomanDateParser.parse(input);
      isoDate = parsed.asIso8601String();
    } catch (ParseException e) {
      warning = e.getMessage();
    }
    return new MultiValueResult()
        .with("date", isoDate)
        .with("warn", warning);
  }

}
