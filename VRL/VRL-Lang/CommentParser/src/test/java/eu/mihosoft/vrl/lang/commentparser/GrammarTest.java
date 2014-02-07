/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.commentparser;

import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsBaseListener;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsLexer;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsListener;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class GrammarTest {

    @BeforeClass
    public static void setUpClass() {
        //
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testComments() {

        final List<Integer> types = new ArrayList<>();

        try {
            parse("Comments01.txt", new CommentsBaseListener() {

                @Override
                public void enterLineComment(CommentsParser.LineCommentContext ctx) {
                    super.enterLineComment(ctx);
                    types.add(CommentsParser.RULE_lineComment);
                }

                @Override
                public void enterPlainMultiLineComment(CommentsParser.PlainMultiLineCommentContext ctx) {
                    super.enterPlainMultiLineComment(ctx);
                    types.add(CommentsParser.RULE_plainMultiLineComment);
                }

                @Override
                public void enterJavadocComment(CommentsParser.JavadocCommentContext ctx) {
                    super.enterJavadocComment(ctx);

                    types.add(CommentsParser.RULE_javadocComment);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("3 comments expected, got " + types.size(), types.size() == 3);

        Assert.assertTrue("line comment expected", types.get(0) == CommentsParser.RULE_lineComment);
        Assert.assertTrue("plain multiline comment expected", types.get(1) == CommentsParser.RULE_plainMultiLineComment);
        Assert.assertTrue("javadoc comment expected", types.get(2) == CommentsParser.RULE_javadocComment);
    }

    @Test
    public void testOverlap() {

//        final List<Integer> types = new ArrayList<>();
        final List<Integer> lineComments = new ArrayList<>();
        final List<Integer> plainMultiLineComments = new ArrayList<>();
        final List<Integer> strings = new ArrayList<>();
        final List<Integer> javadocComments = new ArrayList<>();

        try {
            parse("Overlap01.txt", new CommentsBaseListener() {

                @Override
                public void enterLineComment(CommentsParser.LineCommentContext ctx) {
                    super.enterLineComment(ctx);
                    lineComments.add(CommentsParser.RULE_lineComment);
                }

                @Override
                public void enterPlainMultiLineComment(CommentsParser.PlainMultiLineCommentContext ctx) {
                    super.enterPlainMultiLineComment(ctx);
                    plainMultiLineComments.add(CommentsParser.RULE_plainMultiLineComment);
                }

                @Override
                public void enterJavadocComment(CommentsParser.JavadocCommentContext ctx) {
                    super.enterJavadocComment(ctx);

                    javadocComments.add(CommentsParser.RULE_javadocComment);
                }

                @Override
                public void enterString(CommentsParser.StringContext ctx) {
                    super.enterString(ctx);

                    strings.add(CommentsParser.RULE_string);
                }

            });
        } catch (IOException ex) {
            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("2 strings expected, got " + strings.size(), strings.size() == 2);
        Assert.assertTrue("4 line comments expected, got " + lineComments.size(), lineComments.size() == 4);
        Assert.assertTrue("0 javadoc comments expected, got " + javadocComments.size(), javadocComments.isEmpty());
        Assert.assertTrue("2 plain multiline comments expected, got " + plainMultiLineComments.size(), plainMultiLineComments.size() == 2);

    }

    @Test
    public void testStringEscapes() {

//        final List<Integer> types = new ArrayList<>();
        final List<String> strings = new ArrayList<>();
        final List<String> doubleQuotes = new ArrayList<>();
        final List<String> singleQuotes = new ArrayList<>();

        try {
            parse("Escape01.txt", new CommentsBaseListener() {

                @Override
                public void enterStringSingleQuote(CommentsParser.StringSingleQuoteContext ctx) {
                    super.enterStringSingleQuote(ctx);
                    singleQuotes.add(ctx.getText());
                }

                @Override
                public void enterStringDoubleQuotes(CommentsParser.StringDoubleQuotesContext ctx) {
                    super.enterStringDoubleQuotes(ctx);
                    doubleQuotes.add(ctx.getText());
                }

                @Override
                public void enterString(CommentsParser.StringContext ctx) {
                    super.enterString(ctx);

                    strings.add(ctx.getText());
                }

            });
        } catch (IOException ex) {
            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("4 strings expected, got " + strings.size(), strings.size() == 4);
        Assert.assertTrue("2 single-quote strings expected, got " + singleQuotes.size(), singleQuotes.size() == 2);
        Assert.assertTrue("2 double-quote strings expected, got " + doubleQuotes.size(), doubleQuotes.size() == 2);

        Assert.assertEquals("\"this is an escaped \\\" (double quote)\"", doubleQuotes.get(0));
        Assert.assertEquals("'this is an escaped \\\' (single quote)'", singleQuotes.get(0));

        Assert.assertEquals("\"we use ' inside \\\" env\"", doubleQuotes.get(1));
        Assert.assertEquals("'we use \" inside \\' env'", singleQuotes.get(1));
    }

    @Test
    public void testRealCode01() {

        final List<String> lineComments = new ArrayList<>();
        final List<String> plainMultiLineComments = new ArrayList<>();
        final List<String> javadocComments = new ArrayList<>();

        try {
            parse("RealCode01.txt", new CommentsBaseListener() {

                @Override
                public void enterLineComment(CommentsParser.LineCommentContext ctx) {
                    super.enterLineComment(ctx);
                    lineComments.add(ctx.getText());
                }

                @Override
                public void enterPlainMultiLineComment(CommentsParser.PlainMultiLineCommentContext ctx) {
                    super.enterPlainMultiLineComment(ctx);

                    plainMultiLineComments.add(ctx.getText());
                }

                @Override
                public void enterJavadocComment(CommentsParser.JavadocCommentContext ctx) {
                    super.enterJavadocComment(ctx);

                    javadocComments.add(ctx.getText());
                }

            });
        } catch (IOException ex) {
            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("21 line comments expected, got " + lineComments.size(), lineComments.size() == 21);
        Assert.assertTrue("1 plain multiline comment expected, got " + plainMultiLineComments.size(), plainMultiLineComments.size() == 1);
        Assert.assertTrue("1 javadoc comment expected, got " + javadocComments.size(), javadocComments.size() == 1);
    }

    @Test
    public void testRealCode02() {

        final List<String> lineComments = new ArrayList<>();
        final List<String> plainMultiLineComments = new ArrayList<>();
        final List<String> javadocComments = new ArrayList<>();

        try {
            parse("RealCode02.txt", new CommentsBaseListener() {

                @Override
                public void enterLineComment(CommentsParser.LineCommentContext ctx) {
                    super.enterLineComment(ctx);
                    lineComments.add(ctx.getText());
                }

                @Override
                public void enterPlainMultiLineComment(CommentsParser.PlainMultiLineCommentContext ctx) {
                    super.enterPlainMultiLineComment(ctx);

                    plainMultiLineComments.add(ctx.getText());
                }

                @Override
                public void enterJavadocComment(CommentsParser.JavadocCommentContext ctx) {
                    super.enterJavadocComment(ctx);

                    javadocComments.add(ctx.getText());
                }

            });
        } catch (IOException ex) {
            Logger.getLogger(GrammarTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("2 line comments expected, got " + lineComments.size(), lineComments.size() == 2);
        Assert.assertTrue("1 plain multiline comment expected, got " + plainMultiLineComments.size(), plainMultiLineComments.size() == 1);
        Assert.assertTrue("31 javadoc comment expected, got " + javadocComments.size(), javadocComments.size() == 31);
    }

    

    public static InputStream getResourceAsStream(String resourceName) {
        return GrammarTest.class.getResourceAsStream("/eu/mihosoft/vrl/lang/commentparser/" + resourceName);
    }

    public static void parse(String resourceName, CommentsListener l) throws IOException {
        parse(GrammarTest.class.getResourceAsStream("/eu/mihosoft/vrl/lang/commentparser/" + resourceName), l);
    }

    public static void parse(InputStream is, CommentsListener l) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(is);

        CommentsLexer lexer = new CommentsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentsParser parser = new CommentsParser(tokens);

        ParserRuleContext tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        CommentsListener extractor = l;
        walker.walk(extractor, tree);
    }

}
