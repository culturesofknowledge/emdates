package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractConverterTest {
  protected abstract Function<TestData.TestRow, Arguments> mapData();

  private Stream<Arguments> data2019() throws Exception {
    return TestData.getData("2019.csv").map(mapData());
  }

  private Stream<Arguments> data2018() throws Exception {
    return TestData.getData("2018.csv").map(mapData());
  }

  private Stream<Arguments> data2017() throws Exception {
    return TestData.getData("2017.csv").map(mapData());
  }

  private Stream<Arguments> data2016() throws Exception {
    return TestData.getData("2016.csv").map(mapData());
  }

  private Stream<Arguments> data2015() throws Exception {
    return TestData.getData("2015.csv").map(mapData());
  }

  private Stream<Arguments> data2014() throws Exception {
    return TestData.getData("2014.csv").map(mapData());
  }

  private Stream<Arguments> data2013() throws Exception {
    return TestData.getData("2013.csv").map(mapData());
  }

  private Stream<Arguments> data2012() throws Exception {
    return TestData.getData("2012.csv").map(mapData());
  }

  private Stream<Arguments> data2011() throws Exception {
    return TestData.getData("2011.csv").map(mapData());
  }

  private Stream<Arguments> data2010() throws Exception {
    return TestData.getData("2010.csv").map(mapData());
  }

  private Stream<Arguments> dataDates1() throws Exception {
    return TestData.getData("dates1.csv").map(mapData());
  }

  private Stream<Arguments> dataAddedDates() throws Exception {
    return TestData.getData("added_dates.csv").map(mapData());
  }

  @ParameterizedTest
  @MethodSource({"data2019", "data2018", "data2017", "data2016", "data2015", "data2014", "data2013", "data2012",
      "data2011", "data2010", "dataDates1", "dataAddedDates"
  })
  public void testToRataDie(YearMonthDay gregorianDate, int expectedRataDie) {
    int rataDie = getInstance().toRataDie(gregorianDate);

    assertThat(rataDie, is(expectedRataDie));
  }

  @ParameterizedTest
  @MethodSource({"data2019", "data2018", "data2017", "data2016", "data2015", "data2014", "data2013", "data2012",
      "data2011", "data2010", "dataDates1", "dataAddedDates"
  })
  public void testFromRataDie(YearMonthDay expectedGregorianDate, int rataDie) {
    YearMonthDay gregorian = getInstance().fromRataDie(rataDie);

    assertThat(gregorian, is(expectedGregorianDate));
  }

  protected abstract CalendarConverter getInstance();
}
