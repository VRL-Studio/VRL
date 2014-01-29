// Generated from /Users/miho/EigeneApps/VRL/VRL/VRL-Lang/CommentParser/src/main/java/eu/mihosoft/vrl/lang/commentparser/antlr/grammar/Comments.g4 by ANTLR 4.1
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