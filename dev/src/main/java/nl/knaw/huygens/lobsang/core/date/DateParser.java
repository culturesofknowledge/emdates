package nl.knaw.huygens.lobsang.core.date;

public interface DateParser {
  AbstractDate fromString(String dateString) throws NotAValidDateException;

  AbstractDate fromRataDie(int rataDie);
}
