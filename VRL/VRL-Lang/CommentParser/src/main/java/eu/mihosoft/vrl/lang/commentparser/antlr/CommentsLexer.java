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
		STRING_DOUBLE=1, STRING_SINGLE=2, JAVADOC_COMMENT=3, VRL_MULTILINE_COMMENT=4, 
		VRL_LINE_COMMENT=5, MULTILINE_COMMENT=6, LINE_COMMENT=7, UNKNOWN=8;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "VRL_MULTILINE_COMMENT", 
		"VRL_LINE_COMMENT", "MULTILINE_COMMENT", "LINE_COMMENT", "UNKNOWN"
	};
	public static final String[] ruleNames = {
		"STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "VRL_MULTILINE_COMMENT", 
		"VRL_LINE_COMMENT", "MULTILINE_COMMENT", "LINE_COMMENT", "UNKNOWN"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\np\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\2"+
		"\7\2\30\n\2\f\2\16\2\33\13\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3#\n\3\f\3\16\3"+
		"&\13\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4/\n\4\f\4\16\4\62\13\4\3\4\3\4\3"+
		"\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5B\n\5\f\5\16\5E\13\5"+
		"\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6U\n\6\f\6"+
		"\16\6X\13\6\3\7\3\7\3\7\3\7\7\7^\n\7\f\7\16\7a\13\7\3\7\3\7\3\7\3\b\3"+
		"\b\3\b\3\b\7\bj\n\b\f\b\16\bm\13\b\3\t\3\t\7\31$\60C_\2\n\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\3\2\7\6\2\f\f\17\17$$^^\4\2$$^^\6\2\f\f\17\17"+
		"))^^\4\2))^^\4\2\f\f\17\17x\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\3\23\3\2\2\2"+
		"\5\36\3\2\2\2\7)\3\2\2\2\t\66\3\2\2\2\13I\3\2\2\2\rY\3\2\2\2\17e\3\2\2"+
		"\2\21n\3\2\2\2\23\31\7$\2\2\24\30\n\2\2\2\25\26\7^\2\2\26\30\t\3\2\2\27"+
		"\24\3\2\2\2\27\25\3\2\2\2\30\33\3\2\2\2\31\32\3\2\2\2\31\27\3\2\2\2\32"+
		"\34\3\2\2\2\33\31\3\2\2\2\34\35\7$\2\2\35\4\3\2\2\2\36$\7)\2\2\37#\n\4"+
		"\2\2 !\7^\2\2!#\t\5\2\2\"\37\3\2\2\2\" \3\2\2\2#&\3\2\2\2$%\3\2\2\2$\""+
		"\3\2\2\2%\'\3\2\2\2&$\3\2\2\2\'(\7)\2\2(\6\3\2\2\2)*\7\61\2\2*+\7,\2\2"+
		"+,\7,\2\2,\60\3\2\2\2-/\13\2\2\2.-\3\2\2\2/\62\3\2\2\2\60\61\3\2\2\2\60"+
		".\3\2\2\2\61\63\3\2\2\2\62\60\3\2\2\2\63\64\7,\2\2\64\65\7\61\2\2\65\b"+
		"\3\2\2\2\66\67\7\61\2\2\678\7,\2\289\7>\2\29:\7#\2\2:;\7X\2\2;<\7T\2\2"+
		"<=\7N\2\2=>\7#\2\2>?\7@\2\2?C\3\2\2\2@B\13\2\2\2A@\3\2\2\2BE\3\2\2\2C"+
		"D\3\2\2\2CA\3\2\2\2DF\3\2\2\2EC\3\2\2\2FG\7,\2\2GH\7\61\2\2H\n\3\2\2\2"+
		"IJ\7\61\2\2JK\7\61\2\2KL\7>\2\2LM\7#\2\2MN\7X\2\2NO\7T\2\2OP\7N\2\2PQ"+
		"\7#\2\2QR\7@\2\2RV\3\2\2\2SU\n\6\2\2TS\3\2\2\2UX\3\2\2\2VT\3\2\2\2VW\3"+
		"\2\2\2W\f\3\2\2\2XV\3\2\2\2YZ\7\61\2\2Z[\7,\2\2[_\3\2\2\2\\^\13\2\2\2"+
		"]\\\3\2\2\2^a\3\2\2\2_`\3\2\2\2_]\3\2\2\2`b\3\2\2\2a_\3\2\2\2bc\7,\2\2"+
		"cd\7\61\2\2d\16\3\2\2\2ef\7\61\2\2fg\7\61\2\2gk\3\2\2\2hj\n\6\2\2ih\3"+
		"\2\2\2jm\3\2\2\2ki\3\2\2\2kl\3\2\2\2l\20\3\2\2\2mk\3\2\2\2no\13\2\2\2"+
		"o\22\3\2\2\2\f\2\27\31\"$\60CV_k\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}