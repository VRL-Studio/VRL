/* 
 * CommentsLexer.java
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
		STRING_DOUBLE=1, STRING_SINGLE=2, JAVADOC_COMMENT=3, MULTILINE_COMMENT=4, 
		LINE_COMMENT=5, UNKNOWN=6;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "MULTILINE_COMMENT", 
		"LINE_COMMENT", "UNKNOWN"
	};
	public static final String[] ruleNames = {
		"STRING_DOUBLE", "STRING_SINGLE", "JAVADOC_COMMENT", "MULTILINE_COMMENT", 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\bI\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\2\7\2\24\n\2\f\2"+
		"\16\2\27\13\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3\37\n\3\f\3\16\3\"\13\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\3\4\7\4+\n\4\f\4\16\4.\13\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\5\7\5\67\n\5\f\5\16\5:\13\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\7\6C\n\6\f"+
		"\6\16\6F\13\6\3\7\3\7\6\25 ,8\2\b\3\3\5\4\7\5\t\6\13\7\r\b\3\2\7\6\2\f"+
		"\f\17\17$$^^\4\2$$^^\6\2\f\f\17\17))^^\4\2))^^\4\2\f\f\17\17O\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\3\17"+
		"\3\2\2\2\5\32\3\2\2\2\7%\3\2\2\2\t\62\3\2\2\2\13>\3\2\2\2\rG\3\2\2\2\17"+
		"\25\7$\2\2\20\24\n\2\2\2\21\22\7^\2\2\22\24\t\3\2\2\23\20\3\2\2\2\23\21"+
		"\3\2\2\2\24\27\3\2\2\2\25\26\3\2\2\2\25\23\3\2\2\2\26\30\3\2\2\2\27\25"+
		"\3\2\2\2\30\31\7$\2\2\31\4\3\2\2\2\32 \7)\2\2\33\37\n\4\2\2\34\35\7^\2"+
		"\2\35\37\t\5\2\2\36\33\3\2\2\2\36\34\3\2\2\2\37\"\3\2\2\2 !\3\2\2\2 \36"+
		"\3\2\2\2!#\3\2\2\2\" \3\2\2\2#$\7)\2\2$\6\3\2\2\2%&\7\61\2\2&\'\7,\2\2"+
		"\'(\7,\2\2(,\3\2\2\2)+\13\2\2\2*)\3\2\2\2+.\3\2\2\2,-\3\2\2\2,*\3\2\2"+
		"\2-/\3\2\2\2.,\3\2\2\2/\60\7,\2\2\60\61\7\61\2\2\61\b\3\2\2\2\62\63\7"+
		"\61\2\2\63\64\7,\2\2\648\3\2\2\2\65\67\13\2\2\2\66\65\3\2\2\2\67:\3\2"+
		"\2\289\3\2\2\28\66\3\2\2\29;\3\2\2\2:8\3\2\2\2;<\7,\2\2<=\7\61\2\2=\n"+
		"\3\2\2\2>?\7\61\2\2?@\7\61\2\2@D\3\2\2\2AC\n\6\2\2BA\3\2\2\2CF\3\2\2\2"+
		"DB\3\2\2\2DE\3\2\2\2E\f\3\2\2\2FD\3\2\2\2GH\13\2\2\2H\16\3\2\2\2\n\2\23"+
		"\25\36 ,8D\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}