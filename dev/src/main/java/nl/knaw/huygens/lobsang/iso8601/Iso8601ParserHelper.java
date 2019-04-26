package nl.knaw.huygens.lobsang.iso8601;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Iso8601ParserHelper {
  private Iso8601ParserHelper() {

  }
  public static Iso8601Date parse(String dateString) {
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final Iso8601FormatParser.Iso8601Context iso8601 = iso8601FormatParser.iso8601();
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);

    return builder.build();
  }
}
