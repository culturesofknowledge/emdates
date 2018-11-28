package nl.knaw.huygens.lobsang.core.converters;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Function;

public class GregorianConverterTest extends AbstractConverterTest {

  private static GregorianConverter instance;

  @BeforeAll
  public static void setUp() {
    instance = new GregorianConverter();
  }

  @Override
  protected GregorianConverter getInstance() {
    return instance;
  }

  @Override
  protected Function<TestData.TestRow, Arguments> mapData() {
    return row -> Arguments.of(row.getGregorian(), row.getRataDie());
  }
}
