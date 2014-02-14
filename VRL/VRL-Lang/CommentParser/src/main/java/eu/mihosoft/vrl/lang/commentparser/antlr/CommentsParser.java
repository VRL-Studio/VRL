// Generated from /Users/miho/EigeneApps/VRL/VRL/VRL-Lang/CommentParser/src/main/java/eu/mihosoft/vrl/lang/commentparser/antlr/grammar/Comments.g4 by ANTLR 4.2
package eu.mihosoft.vrl.lang.commentparser.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommentsParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING_DOUBLE=1, STRING_SINGLE=2, JAVADOC_COMMENT=3, VRL_COMMENT=4, MULTILINE_COMMENT=5, 
		LINE_COMMENT=6, UNKNOWN=7;
	public static final String[] tokenNames = {
		"<INVALID>", "STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "VRL_COMMENT", 
		"MULTILINE_COMMENT", "LINE_COMMENT", "UNKNOWN"
	};
	public static final int
		RULE_program = 0, RULE_comment = 1, RULE_multiLineComment = 2, RULE_plainMultiLineComment = 3, 
		RULE_javadocComment = 4, RULE_vrlComment = 5, RULE_lineComment = 6, RULE_string = 7, 
		RULE_stringDoubleQuotes = 8, RULE_stringSingleQuote = 9, RULE_unknowns = 10;
	public static final String[] ruleNames = {
		"program", "comment", "multiLineComment", "plainMultiLineComment", "javadocComment", 
		"vrlComment", "lineComment", "string", "stringDoubleQuotes", "stringSingleQuote", 
		"unknowns"
	};

	@Override
	public String getGrammarFileName() { return "Comments.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CommentsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public StringContext string(int i) {
			return getRuleContext(StringContext.class,i);
		}
		public List<StringContext> string() {
			return getRuleContexts(StringContext.class);
		}
		public UnknownsContext unknowns(int i) {
			return getRuleContext(UnknownsContext.class,i);
		}
		public List<UnknownsContext> unknowns() {
			return getRuleContexts(UnknownsContext.class);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING_DOUBLE) | (1L << STRING_SINGLE) | (1L << JAVADOC_COMMENT) | (1L << VRL_COMMENT) | (1L << MULTILINE_COMMENT) | (1L << LINE_COMMENT) | (1L << UNKNOWN))) != 0)) {
				{
				setState(25);
				switch (_input.LA(1)) {
				case JAVADOC_COMMENT:
				case VRL_COMMENT:
				case MULTILINE_COMMENT:
				case LINE_COMMENT:
					{
					setState(22); comment();
					}
					break;
				case STRING_DOUBLE:
				case STRING_SINGLE:
					{
					setState(23); string();
					}
					break;
				case UNKNOWN:
					{
					setState(24); unknowns();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommentContext extends ParserRuleContext {
		public MultiLineCommentContext multiLineComment() {
			return getRuleContext(MultiLineCommentContext.class,0);
		}
		public LineCommentContext lineComment() {
			return getRuleContext(LineCommentContext.class,0);
		}
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_comment);
		try {
			setState(32);
			switch (_input.LA(1)) {
			case JAVADOC_COMMENT:
			case VRL_COMMENT:
			case MULTILINE_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(30); multiLineComment();
				}
				break;
			case LINE_COMMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(31); lineComment();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiLineCommentContext extends ParserRuleContext {
		public PlainMultiLineCommentContext plainMultiLineComment() {
			return getRuleContext(PlainMultiLineCommentContext.class,0);
		}
		public JavadocCommentContext javadocComment() {
			return getRuleContext(JavadocCommentContext.class,0);
		}
		public VrlCommentContext vrlComment() {
			return getRuleContext(VrlCommentContext.class,0);
		}
		public MultiLineCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiLineComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterMultiLineComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitMultiLineComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitMultiLineComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiLineCommentContext multiLineComment() throws RecognitionException {
		MultiLineCommentContext _localctx = new MultiLineCommentContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_multiLineComment);
		try {
			setState(37);
			switch (_input.LA(1)) {
			case MULTILINE_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(34); plainMultiLineComment();
				}
				break;
			case JAVADOC_COMMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(35); javadocComment();
				}
				break;
			case VRL_COMMENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(36); vrlComment();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PlainMultiLineCommentContext extends ParserRuleContext {
		public TerminalNode MULTILINE_COMMENT() { return getToken(CommentsParser.MULTILINE_COMMENT, 0); }
		public PlainMultiLineCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plainMultiLineComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterPlainMultiLineComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitPlainMultiLineComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitPlainMultiLineComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlainMultiLineCommentContext plainMultiLineComment() throws RecognitionException {
		PlainMultiLineCommentContext _localctx = new PlainMultiLineCommentContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_plainMultiLineComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39); match(MULTILINE_COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JavadocCommentContext extends ParserRuleContext {
		public TerminalNode JAVADOC_COMMENT() { return getToken(CommentsParser.JAVADOC_COMMENT, 0); }
		public JavadocCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_javadocComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterJavadocComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitJavadocComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitJavadocComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JavadocCommentContext javadocComment() throws RecognitionException {
		JavadocCommentContext _localctx = new JavadocCommentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_javadocComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41); match(JAVADOC_COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VrlCommentContext extends ParserRuleContext {
		public TerminalNode VRL_COMMENT() { return getToken(CommentsParser.VRL_COMMENT, 0); }
		public VrlCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vrlComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterVrlComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitVrlComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitVrlComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VrlCommentContext vrlComment() throws RecognitionException {
		VrlCommentContext _localctx = new VrlCommentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_vrlComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43); match(VRL_COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineCommentContext extends ParserRuleContext {
		public TerminalNode LINE_COMMENT() { return getToken(CommentsParser.LINE_COMMENT, 0); }
		public LineCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lineComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterLineComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitLineComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitLineComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineCommentContext lineComment() throws RecognitionException {
		LineCommentContext _localctx = new LineCommentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_lineComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45); match(LINE_COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringContext extends ParserRuleContext {
		public StringDoubleQuotesContext stringDoubleQuotes() {
			return getRuleContext(StringDoubleQuotesContext.class,0);
		}
		public StringSingleQuoteContext stringSingleQuote() {
			return getRuleContext(StringSingleQuoteContext.class,0);
		}
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_string);
		try {
			setState(49);
			switch (_input.LA(1)) {
			case STRING_DOUBLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(47); stringDoubleQuotes();
				}
				break;
			case STRING_SINGLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(48); stringSingleQuote();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringDoubleQuotesContext extends ParserRuleContext {
		public TerminalNode STRING_DOUBLE() { return getToken(CommentsParser.STRING_DOUBLE, 0); }
		public StringDoubleQuotesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringDoubleQuotes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterStringDoubleQuotes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitStringDoubleQuotes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitStringDoubleQuotes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringDoubleQuotesContext stringDoubleQuotes() throws RecognitionException {
		StringDoubleQuotesContext _localctx = new StringDoubleQuotesContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_stringDoubleQuotes);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51); match(STRING_DOUBLE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringSingleQuoteContext extends ParserRuleContext {
		public TerminalNode STRING_SINGLE() { return getToken(CommentsParser.STRING_SINGLE, 0); }
		public StringSingleQuoteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringSingleQuote; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterStringSingleQuote(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitStringSingleQuote(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitStringSingleQuote(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringSingleQuoteContext stringSingleQuote() throws RecognitionException {
		StringSingleQuoteContext _localctx = new StringSingleQuoteContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_stringSingleQuote);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53); match(STRING_SINGLE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnknownsContext extends ParserRuleContext {
		public List<TerminalNode> UNKNOWN() { return getTokens(CommentsParser.UNKNOWN); }
		public TerminalNode UNKNOWN(int i) {
			return getToken(CommentsParser.UNKNOWN, i);
		}
		public UnknownsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unknowns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterUnknowns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitUnknowns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitUnknowns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnknownsContext unknowns() throws RecognitionException {
		UnknownsContext _localctx = new UnknownsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_unknowns);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(56); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(55); match(UNKNOWN);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(58); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			} while ( _alt!=2 && _alt!=-1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\t?\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\3\2\3\2\3\2\7\2\34\n\2\f\2\16\2\37\13\2\3\3\3\3\5\3#\n\3\3\4\3"+
		"\4\3\4\5\4(\n\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\5\t\64\n\t\3\n"+
		"\3\n\3\13\3\13\3\f\6\f;\n\f\r\f\16\f<\3\f\2\2\r\2\4\6\b\n\f\16\20\22\24"+
		"\26\2\2;\2\35\3\2\2\2\4\"\3\2\2\2\6\'\3\2\2\2\b)\3\2\2\2\n+\3\2\2\2\f"+
		"-\3\2\2\2\16/\3\2\2\2\20\63\3\2\2\2\22\65\3\2\2\2\24\67\3\2\2\2\26:\3"+
		"\2\2\2\30\34\5\4\3\2\31\34\5\20\t\2\32\34\5\26\f\2\33\30\3\2\2\2\33\31"+
		"\3\2\2\2\33\32\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36\3"+
		"\3\2\2\2\37\35\3\2\2\2 #\5\6\4\2!#\5\16\b\2\" \3\2\2\2\"!\3\2\2\2#\5\3"+
		"\2\2\2$(\5\b\5\2%(\5\n\6\2&(\5\f\7\2\'$\3\2\2\2\'%\3\2\2\2\'&\3\2\2\2"+
		"(\7\3\2\2\2)*\7\7\2\2*\t\3\2\2\2+,\7\5\2\2,\13\3\2\2\2-.\7\6\2\2.\r\3"+
		"\2\2\2/\60\7\b\2\2\60\17\3\2\2\2\61\64\5\22\n\2\62\64\5\24\13\2\63\61"+
		"\3\2\2\2\63\62\3\2\2\2\64\21\3\2\2\2\65\66\7\3\2\2\66\23\3\2\2\2\678\7"+
		"\4\2\28\25\3\2\2\29;\7\t\2\2:9\3\2\2\2;<\3\2\2\2<:\3\2\2\2<=\3\2\2\2="+
		"\27\3\2\2\2\b\33\35\"\'\63<";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}