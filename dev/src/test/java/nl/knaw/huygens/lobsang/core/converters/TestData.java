package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;


public class TestData {

  private static final int GREG_YEAR = 5;
  private static final int GREG_MONTH = 6;
  private static final int GREG_DAY = 7;
  private static final int JUL_YEAR = 8;
  private static final int JUL_MONTH = 9;
  private static final int JUL_DAY = 10;
  private static final int JULIAN_DAY = 2;
  private static final int RATA_DIE = 0;

  public static Stream<TestRow> getData(String fileName) throws URISyntaxException, IOException {
    URL resource = TestData.class.getResource(fileName);
    File dataFile = new File(resource.toURI());
    try (FileReader dataReader = new FileReader(dataFile)) {
      CSVParser parse = CSVFormat.newFormat(',').parse(dataReader);
      List<CSVRecord> records = parse.getRecords();
      return records.subList(2, records.size() - 1).stream().map(rec -> new TestRow()
          .setGregorian(getYearMonthDay(rec, GREG_YEAR, GREG_MONTH, GREG_DAY))
          .setJulian(getYearMonthDay(rec, JUL_YEAR, JUL_MONTH, JUL_DAY))
          .setJulianDay(Double.parseDouble(rec.get(JULIAN_DAY).trim()))
          .setRataDie(parseInt(rec.get(RATA_DIE).trim()))
      );
    }
  }

  private static YearMonthDay getYearMonthDay(CSVRecord rec, int yearIndex, int monthIndex, int dayIndex) {
    return new YearMonthDay(asInt(rec, yearIndex), asInt(rec, monthIndex), asInt(rec, dayIndex));
  }

  private static int asInt(CSVRecord rec, int yearIndex) {
    return parseInt(rec.get(yearIndex).trim());
  }

  public static class TestRow {
    private YearMonthDay gregorian;
    private YearMonthDay julian;
    private double julianDay;
    private int rataDie;

    private TestRow() {
    }

    public YearMonthDay getGregorian() {
      return gregorian;
    }

    private TestRow setGregorian(YearMonthDay gregorian) {
      this.gregorian = gregorian;
      return this;
    }

    public YearMonthDay getJulian() {
      return julian;
    }

    private TestRow setJulian(YearMonthDay julian) {
      this.julian = julian;
      return this;
    }

    public double getJulianDay() {
      return julianDay;
    }

    private TestRow setJulianDay(double julianDay) {
      this.julianDay = julianDay;
      return this;
    }

    public int getRataDie() {
      return rataDie;
    }

    private TestRow setRataDie(int rataDie) {
      this.rataDie = rataDie;
      return this;
    }
  }
}
