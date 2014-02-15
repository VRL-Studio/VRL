/* 
 * CommentsParser.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, in press.
 */

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
		STRING_DOUBLE=1, STRING_SINGLE=2, JAVADOC_COMMENT=3, VRL_MULTILINE_COMMENT=4, 
		VRL_LINE_COMMENT=5, MULTILINE_COMMENT=6, LINE_COMMENT=7, UNKNOWN=8;
	public static final String[] tokenNames = {
		"<INVALID>", "STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "VRL_MULTILINE_COMMENT", 
		"VRL_LINE_COMMENT", "MULTILINE_COMMENT", "LINE_COMMENT", "UNKNOWN"
	};
	public static final int
		RULE_program = 0, RULE_comment = 1, RULE_multiLineComment = 2, RULE_plainMultiLineComment = 3, 
		RULE_javadocComment = 4, RULE_vrlMultiLineComment = 5, RULE_vrlLineComment = 6, 
		RULE_lineComment = 7, RULE_plainLineComment = 8, RULE_string = 9, RULE_stringDoubleQuotes = 10, 
		RULE_stringSingleQuote = 11, RULE_unknowns = 12;
	public static final String[] ruleNames = {
		"program", "comment", "multiLineComment", "plainMultiLineComment", "javadocComment", 
		"vrlMultiLineComment", "vrlLineComment", "lineComment", "plainLineComment", 
		"string", "stringDoubleQuotes", "stringSingleQuote", "unknowns"
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
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING_DOUBLE) | (1L << STRING_SINGLE) | (1L << JAVADOC_COMMENT) | (1L << VRL_MULTILINE_COMMENT) | (1L << VRL_LINE_COMMENT) | (1L << MULTILINE_COMMENT) | (1L << LINE_COMMENT) | (1L << UNKNOWN))) != 0)) {
				{
				setState(29);
				switch (_input.LA(1)) {
				case JAVADOC_COMMENT:
				case VRL_MULTILINE_COMMENT:
				case VRL_LINE_COMMENT:
				case MULTILINE_COMMENT:
				case LINE_COMMENT:
					{
					setState(26); comment();
					}
					break;
				case STRING_DOUBLE:
				case STRING_SINGLE:
					{
					setState(27); string();
					}
					break;
				case UNKNOWN:
					{
					setState(28); unknowns();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(33);
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
			setState(36);
			switch (_input.LA(1)) {
			case JAVADOC_COMMENT:
			case VRL_MULTILINE_COMMENT:
			case MULTILINE_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(34); multiLineComment();
				}
				break;
			case VRL_LINE_COMMENT:
			case LINE_COMMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(35); lineComment();
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
		public VrlMultiLineCommentContext vrlMultiLineComment() {
			return getRuleContext(VrlMultiLineCommentContext.class,0);
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
			setState(41);
			switch (_input.LA(1)) {
			case MULTILINE_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(38); plainMultiLineComment();
				}
				break;
			case JAVADOC_COMMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(39); javadocComment();
				}
				break;
			case VRL_MULTILINE_COMMENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(40); vrlMultiLineComment();
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
			setState(43); match(MULTILINE_COMMENT);
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
			setState(45); match(JAVADOC_COMMENT);
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

	public static class VrlMultiLineCommentContext extends ParserRuleContext {
		public TerminalNode VRL_MULTILINE_COMMENT() { return getToken(CommentsParser.VRL_MULTILINE_COMMENT, 0); }
		public VrlMultiLineCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vrlMultiLineComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterVrlMultiLineComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitVrlMultiLineComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitVrlMultiLineComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VrlMultiLineCommentContext vrlMultiLineComment() throws RecognitionException {
		VrlMultiLineCommentContext _localctx = new VrlMultiLineCommentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_vrlMultiLineComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47); match(VRL_MULTILINE_COMMENT);
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

	public static class VrlLineCommentContext extends ParserRuleContext {
		public TerminalNode VRL_LINE_COMMENT() { return getToken(CommentsParser.VRL_LINE_COMMENT, 0); }
		public VrlLineCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vrlLineComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterVrlLineComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitVrlLineComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitVrlLineComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VrlLineCommentContext vrlLineComment() throws RecognitionException {
		VrlLineCommentContext _localctx = new VrlLineCommentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_vrlLineComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49); match(VRL_LINE_COMMENT);
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
		public VrlLineCommentContext vrlLineComment() {
			return getRuleContext(VrlLineCommentContext.class,0);
		}
		public PlainLineCommentContext plainLineComment() {
			return getRuleContext(PlainLineCommentContext.class,0);
		}
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
		enterRule(_localctx, 14, RULE_lineComment);
		try {
			setState(53);
			switch (_input.LA(1)) {
			case LINE_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(51); plainLineComment();
				}
				break;
			case VRL_LINE_COMMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(52); vrlLineComment();
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

	public static class PlainLineCommentContext extends ParserRuleContext {
		public TerminalNode LINE_COMMENT() { return getToken(CommentsParser.LINE_COMMENT, 0); }
		public PlainLineCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plainLineComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).enterPlainLineComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentsListener ) ((CommentsListener)listener).exitPlainLineComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentsVisitor ) return ((CommentsVisitor<? extends T>)visitor).visitPlainLineComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlainLineCommentContext plainLineComment() throws RecognitionException {
		PlainLineCommentContext _localctx = new PlainLineCommentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_plainLineComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55); match(LINE_COMMENT);
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
		enterRule(_localctx, 18, RULE_string);
		try {
			setState(59);
			switch (_input.LA(1)) {
			case STRING_DOUBLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(57); stringDoubleQuotes();
				}
				break;
			case STRING_SINGLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(58); stringSingleQuote();
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
		enterRule(_localctx, 20, RULE_stringDoubleQuotes);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61); match(STRING_DOUBLE);
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
		enterRule(_localctx, 22, RULE_stringSingleQuote);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63); match(STRING_SINGLE);
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
		enterRule(_localctx, 24, RULE_unknowns);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(66); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(65); match(UNKNOWN);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(68); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\nI\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\2\7\2 \n\2\f\2\16\2#\13\2\3\3\3\3\5"+
		"\3\'\n\3\3\4\3\4\3\4\5\4,\n\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t"+
		"\5\t8\n\t\3\n\3\n\3\13\3\13\5\13>\n\13\3\f\3\f\3\r\3\r\3\16\6\16E\n\16"+
		"\r\16\16\16F\3\16\2\2\17\2\4\6\b\n\f\16\20\22\24\26\30\32\2\2D\2!\3\2"+
		"\2\2\4&\3\2\2\2\6+\3\2\2\2\b-\3\2\2\2\n/\3\2\2\2\f\61\3\2\2\2\16\63\3"+
		"\2\2\2\20\67\3\2\2\2\229\3\2\2\2\24=\3\2\2\2\26?\3\2\2\2\30A\3\2\2\2\32"+
		"D\3\2\2\2\34 \5\4\3\2\35 \5\24\13\2\36 \5\32\16\2\37\34\3\2\2\2\37\35"+
		"\3\2\2\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"\3\3\2\2\2#!"+
		"\3\2\2\2$\'\5\6\4\2%\'\5\20\t\2&$\3\2\2\2&%\3\2\2\2\'\5\3\2\2\2(,\5\b"+
		"\5\2),\5\n\6\2*,\5\f\7\2+(\3\2\2\2+)\3\2\2\2+*\3\2\2\2,\7\3\2\2\2-.\7"+
		"\b\2\2.\t\3\2\2\2/\60\7\5\2\2\60\13\3\2\2\2\61\62\7\6\2\2\62\r\3\2\2\2"+
		"\63\64\7\7\2\2\64\17\3\2\2\2\658\5\22\n\2\668\5\16\b\2\67\65\3\2\2\2\67"+
		"\66\3\2\2\28\21\3\2\2\29:\7\t\2\2:\23\3\2\2\2;>\5\26\f\2<>\5\30\r\2=;"+
		"\3\2\2\2=<\3\2\2\2>\25\3\2\2\2?@\7\3\2\2@\27\3\2\2\2AB\7\4\2\2B\31\3\2"+
		"\2\2CE\7\n\2\2DC\3\2\2\2EF\3\2\2\2FD\3\2\2\2FG\3\2\2\2G\33\3\2\2\2\t\37"+
		"!&+\67=F";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}