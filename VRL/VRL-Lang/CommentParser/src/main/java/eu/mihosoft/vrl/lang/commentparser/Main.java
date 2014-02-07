/* 
 * Main.java
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

package eu.mihosoft.vrl.lang.commentparser;

import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsLexer;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String code = ""
                + "/**\n"
                + " * JavaDoc ...\n"
                + "*/\n"
                + "class Test { \n"
                + "// comment\n"
                + "int value;\n"
                + "          public void test() {\n"
                + "  for() {}\n"
                + "/* TEST 123 */\n"
                + "\"/** T inside string \n*/\"\n"
                + "    println(\"abc\");\n"
                + "'single string // comment inside string'\n"
                + "// comment: \"string inside comment\"\n"
                + "}\n"
                + "}";

//        System.out.println("orig:\n " + code);
        InputStream is = new ByteArrayInputStream(code.getBytes("UTF-8"));

        ANTLRInputStream input = new ANTLRInputStream(is);

        CommentsLexer lexer = new CommentsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentsParser parser = new CommentsParser(tokens);

        ParserRuleContext tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
//        CommentsListener extractor = new MyListener(parser);
//        walker.walk(extractor, tree);
    }

}

//class MyListener extends CommentsBaseListener {
//
//    public MyListener(CommentsParser parser) {
//    }
//
//    @Override
//    public void enterComment(CommentsParser.CommentContext ctx) {
//        super.enterComment(ctx);
//
//        System.out.println("enter comment: " + ctx.getText());
//    }
//
//    @Override
//    public void exitComment(CommentsParser.CommentContext ctx) {
//        super.enterComment(ctx);
//
//        System.out.println("exit comment: " + ctx.getText());
//    }
//
//    @Override
//    public void enterLineComment(CommentsParser.LineCommentContext ctx) {
//        super.enterLineComment(ctx);
//
//        System.out.println("enter line-comment: " + ctx.getText());
//    }
//
//    @Override
//    public void exitLineComment(CommentsParser.LineCommentContext ctx) {
//        super.exitLineComment(ctx);
//
//        System.out.println("exit line-comment: " + ctx.getText());
//    }
//
//    @Override
//    public void enterString(CommentsParser.StringContext ctx) {
//        super.enterString(ctx);
//
//        System.out.println("enter string: " + ctx.getText());
//    }
//
//    @Override
//    public void exitString(CommentsParser.StringContext ctx) {
//        super.exitString(ctx);
//
//        System.out.println("exit string: " + ctx.getText());
//    }
//    
//    
//
////    @Override
////    public void enterUnknowns(CommentsParser.UnknownsContext ctx) {
////        super.enterUnknowns(ctx); //To change body of generated methods, choose Tools | Templates.
////        
////        System.out.println("enter unknown: " + ctx.getText());
////    }
////
////    @Override
////    public void exitUnknowns(CommentsParser.UnknownsContext ctx) {
////        super.exitUnknowns(ctx); //To change body of generated methods, choose Tools | Templates.
////        System.out.println("exit unknown: " + ctx.getText());
////    }
//
//    @Override
//    public void enterJavadocComment(CommentsParser.JavadocCommentContext ctx) {
//        super.enterJavadocComment(ctx); //To change body of generated methods, choose Tools | Templates.
//        
//        System.out.println("enter-javadoc-comment: " + ctx.getText());
//    }
//    
//        @Override
//    public void enterMultiLineComment(CommentsParser.MultiLineCommentContext ctx) {
//        super.enterMultiLineComment(ctx); //To change body of generated methods, choose Tools | Templates.
//        
//        System.out.println("enter-multiline-comment: " + ctx.getText());
//    }
//
//    @Override
//    public void enterPlainMultiLineComment(CommentsParser.PlainMultiLineCommentContext ctx) {
//        super.enterPlainMultiLineComment(ctx); //To change body of generated methods, choose Tools | Templates.
//    
//        System.out.println("enter-plain-multiline-comment: " + ctx.getText());
//    }
//    
//    
//}
