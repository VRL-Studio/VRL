// Generated from /Users/miho/EigeneApps/VRL/VRL/VRL-Lang/CommentParser/src/main/java/eu/mihosoft/vrl/lang/commentparser/antlr/grammar/Comments.g4 by ANTLR 4.1
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
	 * Visit a parse tree produced by {@link CommentsParser#plainMultiLineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlainMultiLineComment(@NotNull CommentsParser.PlainMultiLineCommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#unknowns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnknowns(@NotNull CommentsParser.UnknownsContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(@NotNull CommentsParser.CommentContext ctx);

	/**
	 * Visit a parse tree produced by {@link CommentsParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull CommentsParser.ProgramContext ctx);

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
	 * Visit a parse tree produced by {@link CommentsParser#stringDoubleQuotes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringDoubleQuotes(@NotNull CommentsParser.StringDoubleQuotesContext ctx);
}