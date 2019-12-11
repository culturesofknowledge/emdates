package nl.knaw.huygens.lobsang.iso8601;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.BitSet;

public class Iso8601ParserHelper {
  private Iso8601ParserHelper() {

  }

  public static Iso8601Date parse(String dateString) throws UnsupportedIso8601DateException {
    final Iso8601FormatLexer lexer = new Iso8601FormatLexer(CharStreams.fromString(dateString));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Iso8601FormatParser iso8601FormatParser = new Iso8601FormatParser(tokens);
    final StringBuilder errorMessages = new StringBuilder();
    iso8601FormatParser.addErrorListener(new ANTLRErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                              String msg, RecognitionException e) {
        errorMessages.append(String.format("line %d:%d '%s' ", line, charPositionInLine, msg));
      }

      @Override
      public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
                                  BitSet ambigAlts, ATNConfigSet configs) {
      }

      @Override
      public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                              BitSet conflictingAlts, ATNConfigSet configs) {
      }

      @Override
      public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
                                           ATNConfigSet configs) {
      }
    });

    final Iso8601FormatParser.Iso8601Context iso8601 = iso8601FormatParser.iso8601();
    if (errorMessages.length() > 0) {
      throw new UnsupportedIso8601DateException(errorMessages.toString());
    }
    final Iso8601Date.Iso8601DateBuilder builder = Iso8601Date.builder();
    final Iso8601DateListener listener = new Iso8601DateListener(builder);
    final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
    parseTreeWalker.walk(listener, iso8601);

    return builder.build();
  }
}
