/* 
 * CommentsVisitor.java
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
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CommentsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CommentsVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CommentsParser#multiLineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiLineComment(@NotNull CommentsParser.MultiLineCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(@NotNull CommentsParser.StringContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#stringSingleQuote}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringSingleQuote(@NotNull CommentsParser.StringSingleQuoteContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#unknowns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnknowns(@NotNull CommentsParser.UnknownsContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull CommentsParser.ProgramContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#plainMultiLineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlainMultiLineComment(@NotNull CommentsParser.PlainMultiLineCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(@NotNull CommentsParser.CommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#vrlMultiLineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVrlMultiLineComment(@NotNull CommentsParser.VrlMultiLineCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#lineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLineComment(@NotNull CommentsParser.LineCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#javadocComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavadocComment(@NotNull CommentsParser.JavadocCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#vrlLineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVrlLineComment(@NotNull CommentsParser.VrlLineCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#plainLineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlainLineComment(@NotNull CommentsParser.PlainLineCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#stringDoubleQuotes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringDoubleQuotes(@NotNull CommentsParser.StringDoubleQuotesContext ctx);
}