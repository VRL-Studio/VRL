/* 
 * CommentsListener.java
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
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CommentsParser}.
 */
public interface CommentsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CommentsParser#multiLineComment}.
	 * @param ctx the parse tree
	 */
	void enterMultiLineComment(@NotNull CommentsParser.MultiLineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#multiLineComment}.
	 * @param ctx the parse tree
	 */
	void exitMultiLineComment(@NotNull CommentsParser.MultiLineCommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(@NotNull CommentsParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(@NotNull CommentsParser.StringContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#stringSingleQuote}.
	 * @param ctx the parse tree
	 */
	void enterStringSingleQuote(@NotNull CommentsParser.StringSingleQuoteContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#stringSingleQuote}.
	 * @param ctx the parse tree
	 */
	void exitStringSingleQuote(@NotNull CommentsParser.StringSingleQuoteContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#unknowns}.
	 * @param ctx the parse tree
	 */
	void enterUnknowns(@NotNull CommentsParser.UnknownsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#unknowns}.
	 * @param ctx the parse tree
	 */
	void exitUnknowns(@NotNull CommentsParser.UnknownsContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(@NotNull CommentsParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(@NotNull CommentsParser.ProgramContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#plainMultiLineComment}.
	 * @param ctx the parse tree
	 */
	void enterPlainMultiLineComment(@NotNull CommentsParser.PlainMultiLineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#plainMultiLineComment}.
	 * @param ctx the parse tree
	 */
	void exitPlainMultiLineComment(@NotNull CommentsParser.PlainMultiLineCommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(@NotNull CommentsParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(@NotNull CommentsParser.CommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#vrlMultiLineComment}.
	 * @param ctx the parse tree
	 */
	void enterVrlMultiLineComment(@NotNull CommentsParser.VrlMultiLineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#vrlMultiLineComment}.
	 * @param ctx the parse tree
	 */
	void exitVrlMultiLineComment(@NotNull CommentsParser.VrlMultiLineCommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#lineComment}.
	 * @param ctx the parse tree
	 */
	void enterLineComment(@NotNull CommentsParser.LineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#lineComment}.
	 * @param ctx the parse tree
	 */
	void exitLineComment(@NotNull CommentsParser.LineCommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#javadocComment}.
	 * @param ctx the parse tree
	 */
	void enterJavadocComment(@NotNull CommentsParser.JavadocCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#javadocComment}.
	 * @param ctx the parse tree
	 */
	void exitJavadocComment(@NotNull CommentsParser.JavadocCommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#vrlLineComment}.
	 * @param ctx the parse tree
	 */
	void enterVrlLineComment(@NotNull CommentsParser.VrlLineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#vrlLineComment}.
	 * @param ctx the parse tree
	 */
	void exitVrlLineComment(@NotNull CommentsParser.VrlLineCommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#plainLineComment}.
	 * @param ctx the parse tree
	 */
	void enterPlainLineComment(@NotNull CommentsParser.PlainLineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#plainLineComment}.
	 * @param ctx the parse tree
	 */
	void exitPlainLineComment(@NotNull CommentsParser.PlainLineCommentContext ctx);

	/**
	 * Enter a parse tree produced by {@link CommentsParser#stringDoubleQuotes}.
	 * @param ctx the parse tree
	 */
	void enterStringDoubleQuotes(@NotNull CommentsParser.StringDoubleQuotesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentsParser#stringDoubleQuotes}.
	 * @param ctx the parse tree
	 */
	void exitStringDoubleQuotes(@NotNull CommentsParser.StringDoubleQuotesContext ctx);
}