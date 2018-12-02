package nl.knaw.huygens.lobsang.core.converters;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Function;

public class JulianConverterTest extends AbstractConverterTest {

  private static JulianConverter instance;

  @BeforeAll
  public static void setUp() {
    instance = new JulianConverter();
  }

  @Override
  protected Function<TestData.TestRow, Arguments> mapData() {
    return row -> Arguments.of(row.getJulian(), row.getRataDie());
  }

  @Override
  protected CalendarConverter getInstance() {
    return instance;
  }

}
