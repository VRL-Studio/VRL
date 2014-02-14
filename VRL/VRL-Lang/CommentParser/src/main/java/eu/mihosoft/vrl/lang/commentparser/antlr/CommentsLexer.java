// Generated from /Users/miho/EigeneApps/VRL/VRL/VRL-Lang/CommentParser/src/main/java/eu/mihosoft/vrl/lang/commentparser/antlr/grammar/Comments.g4 by ANTLR 4.2
package eu.mihosoft.vrl.lang.commentparser.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommentsLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING_DOUBLE=1, STRING_SINGLE=2, JAVADOC_COMMENT=3, VRL_COMMENT=4, MULTILINE_COMMENT=5, 
		LINE_COMMENT=6, UNKNOWN=7;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "VRL_COMMENT", "MULTILINE_COMMENT", 
		"LINE_COMMENT", "UNKNOWN"
	};
	public static final String[] ruleNames = {
		"STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "VRL_COMMENT", "MULTILINE_COMMENT", 
		"LINE_COMMENT", "UNKNOWN"
	};


	public CommentsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Comments.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\t^\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\2\3\2\7\2\26"+
		"\n\2\f\2\16\2\31\13\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3!\n\3\f\3\16\3$\13\3"+
		"\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4-\n\4\f\4\16\4\60\13\4\3\4\3\4\3\4\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5@\n\5\f\5\16\5C\13\5\3\5\3"+
		"\5\3\5\3\6\3\6\3\6\3\6\7\6L\n\6\f\6\16\6O\13\6\3\6\3\6\3\6\3\7\3\7\3\7"+
		"\3\7\7\7X\n\7\f\7\16\7[\13\7\3\b\3\b\7\27\".AM\2\t\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\3\2\7\6\2\f\f\17\17$$^^\4\2$$^^\6\2\f\f\17\17))^^\4\2))^^"+
		"\4\2\f\f\17\17e\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\3\21\3\2\2\2\5\34\3\2\2\2\7\'\3\2\2\2"+
		"\t\64\3\2\2\2\13G\3\2\2\2\rS\3\2\2\2\17\\\3\2\2\2\21\27\7$\2\2\22\26\n"+
		"\2\2\2\23\24\7^\2\2\24\26\t\3\2\2\25\22\3\2\2\2\25\23\3\2\2\2\26\31\3"+
		"\2\2\2\27\30\3\2\2\2\27\25\3\2\2\2\30\32\3\2\2\2\31\27\3\2\2\2\32\33\7"+
		"$\2\2\33\4\3\2\2\2\34\"\7)\2\2\35!\n\4\2\2\36\37\7^\2\2\37!\t\5\2\2 \35"+
		"\3\2\2\2 \36\3\2\2\2!$\3\2\2\2\"#\3\2\2\2\" \3\2\2\2#%\3\2\2\2$\"\3\2"+
		"\2\2%&\7)\2\2&\6\3\2\2\2\'(\7\61\2\2()\7,\2\2)*\7,\2\2*.\3\2\2\2+-\13"+
		"\2\2\2,+\3\2\2\2-\60\3\2\2\2./\3\2\2\2.,\3\2\2\2/\61\3\2\2\2\60.\3\2\2"+
		"\2\61\62\7,\2\2\62\63\7\61\2\2\63\b\3\2\2\2\64\65\7\61\2\2\65\66\7,\2"+
		"\2\66\67\7>\2\2\678\7#\2\289\7X\2\29:\7T\2\2:;\7N\2\2;<\7#\2\2<=\7@\2"+
		"\2=A\3\2\2\2>@\13\2\2\2?>\3\2\2\2@C\3\2\2\2AB\3\2\2\2A?\3\2\2\2BD\3\2"+
		"\2\2CA\3\2\2\2DE\7,\2\2EF\7\61\2\2F\n\3\2\2\2GH\7\61\2\2HI\7,\2\2IM\3"+
		"\2\2\2JL\13\2\2\2KJ\3\2\2\2LO\3\2\2\2MN\3\2\2\2MK\3\2\2\2NP\3\2\2\2OM"+
		"\3\2\2\2PQ\7,\2\2QR\7\61\2\2R\f\3\2\2\2ST\7\61\2\2TU\7\61\2\2UY\3\2\2"+
		"\2VX\n\6\2\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\16\3\2\2\2[Y\3\2"+
		"\2\2\\]\13\2\2\2]\20\3\2\2\2\13\2\25\27 \".AMY\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}