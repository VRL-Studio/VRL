/* 
 * VCommentParser.java
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

package eu.mihosoft.vrl.lang;

import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.CommentImpl;
import eu.mihosoft.vrl.lang.model.CommentType;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsBaseListener;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsLexer;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsListener;
import eu.mihosoft.vrl.lang.commentparser.antlr.CommentsParser;
import eu.mihosoft.vrl.lang.model.CodeRange;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.ANTLRInputStream;
import static org.antlr.v4.runtime.ANTLRInputStream.INITIAL_BUFFER_SIZE;
import static org.antlr.v4.runtime.ANTLRInputStream.READ_BUFFER_SIZE;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VCommentParser {

    public static void parse(InputStream is, CommentsListener l, boolean closeStream) throws IOException {
        final ANTLRInputStream input;

        if (closeStream) {
            input = new ANTLRInputStream(is);
        } else {
            input = new ANTLRInputStreamNoClose(is);
        }

        CommentsLexer lexer = new CommentsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentsParser parser = new CommentsParser(tokens);

        ParserRuleContext tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        CommentsListener extractor = l;
        walker.walk(extractor, tree);
    }

    public static void parse(Reader is, CommentsListener l, boolean closeStream) throws IOException {
        final ANTLRInputStream input;

        if (closeStream) {
            input = new ANTLRInputStream(is);
        } else {
            input = new ANTLRInputStreamNoClose(is);
        }

        CommentsLexer lexer = new CommentsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommentsParser parser = new CommentsParser(tokens);

        ParserRuleContext tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        CommentsListener extractor = l;
        walker.walk(extractor, tree);
    }

    public static List<Comment> parse(Reader is) throws IOException {
        return parse(is, false);
    }

    public static List<Comment> parse(Reader is, boolean closeReader) throws IOException {
        final List<Comment> result = new ArrayList<>();

        final Reader reader = is;

        final ANTLRInputStream input;

        if (closeReader) {
            input = new ANTLRInputStream(is);
        } else {
            input = new ANTLRInputStreamNoClose(is);
        }

        final CommentsLexer lexer = new CommentsLexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final CommentsParser parser = new CommentsParser(tokens);

        final CommentsListener l = new CommentsBaseListener() {

            @Override
            public void enterPlainMultiLineComment(CommentsParser.PlainMultiLineCommentContext ctx) {
                String commentText = parser.getTokenStream().getText(ctx.start, ctx.stop);

                CodeRange range = new CodeRange(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), reader);

                Comment comment = new CommentImpl(
                        "COMMENT:UNDEFINED",
                        range,
                        commentText, CommentType.PLAIN_MULTI_LINE);

//                System.out.println("/* ... */ " + range + ", " + commentText);
                result.add(comment);
            }

            @Override
            public void exitPlainMultiLineComment(CommentsParser.PlainMultiLineCommentContext ctx) {
                //
            }

            @Override
            public void enterPlainLineComment(CommentsParser.PlainLineCommentContext ctx) {
                String commentText = parser.getTokenStream().getText(ctx.start, ctx.stop);

                CodeRange range = new CodeRange(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), reader);

                Comment comment = new CommentImpl(
                        "COMMENT:UNDEFINED",
                        range,
                        commentText, CommentType.PLAIN_LINE);

//                System.out.println("// " + range);
                result.add(comment);
            }

            @Override
            public void exitPlainLineComment(CommentsParser.PlainLineCommentContext ctx) {
                //
            }

            @Override
            public void enterJavadocComment(CommentsParser.JavadocCommentContext ctx) {
                String commentText = parser.getTokenStream().getText(ctx.start, ctx.stop);

                CodeRange range = new CodeRange(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), reader);

                Comment comment = new CommentImpl(
                        "COMMENT:UNDEFINED",
                        range,
                        commentText, CommentType.JAVADOC);

//                System.out.println("/** ... */ " + range);
                result.add(comment);
            }

            @Override
            public void exitJavadocComment(CommentsParser.JavadocCommentContext ctx) {
                //
            }
            
            @Override
            public void enterVrlLineComment(CommentsParser.VrlLineCommentContext ctx) {
                String commentText = parser.getTokenStream().getText(ctx.start, ctx.stop);

                CodeRange range = new CodeRange(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), reader);

                Comment comment = new CommentImpl(
                        "COMMENT:UNDEFINED",
                        range,
                        commentText, CommentType.VRL_LINE);

//                System.out.println("/** ... */ " + range);
                result.add(comment);
            }

            @Override
            public void exitVrlLineComment(CommentsParser.VrlLineCommentContext ctx) {
                //
            }

            @Override
            public void enterVrlMultiLineComment(CommentsParser.VrlMultiLineCommentContext ctx) {
                String commentText = parser.getTokenStream().getText(ctx.start, ctx.stop);

                CodeRange range = new CodeRange(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), reader);

                Comment comment = new CommentImpl(
                        "COMMENT:UNDEFINED",
                        range,
                        commentText, CommentType.VRL_MULTI_LINE);

//                System.out.println("/** ... */ " + range);
                result.add(comment);
            }

            @Override
            public void exitVrlMultiLineComment(CommentsParser.VrlMultiLineCommentContext ctx) {
                //
            }
        };

        final ParserRuleContext tree = parser.program();

        final ParseTreeWalker walker = new ParseTreeWalker();
        CommentsListener extractor = l;
        try {
            reader.reset();
        } catch (IOException ex) {
            Logger.getLogger(VCommentParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        walker.walk(extractor, tree);

        return result;
    }
}

class ANTLRInputStreamNoClose extends ANTLRInputStream {

    public ANTLRInputStreamNoClose() {
        super();
    }

    public ANTLRInputStreamNoClose(String input) {
        super(input);
    }

    public ANTLRInputStreamNoClose(char[] data, int numberOfActualCharsInArray) {
        super(data, numberOfActualCharsInArray);
    }

    public ANTLRInputStreamNoClose(Reader r) throws IOException {
        super(r);
    }

    public ANTLRInputStreamNoClose(Reader r, int initialSize) throws IOException {
        super(r, initialSize);
    }

    public ANTLRInputStreamNoClose(Reader r, int initialSize, int readChunkSize) throws IOException {
        super(r, initialSize, readChunkSize);
    }

    public ANTLRInputStreamNoClose(InputStream input) throws IOException {
        super(input);
    }

    public ANTLRInputStreamNoClose(InputStream input, int initialSize) throws IOException {
        super(input, initialSize);
    }

    public ANTLRInputStreamNoClose(InputStream input, int initialSize, int readChunkSize) throws IOException {
        super(input, initialSize, readChunkSize);
    }

    @Override
    public void load(Reader r, int size, int readChunkSize)
            throws IOException {
        if (r == null) {
            return;
        }
        if (size <= 0) {
            size = INITIAL_BUFFER_SIZE;
        }
        if (readChunkSize <= 0) {
            readChunkSize = READ_BUFFER_SIZE;
        }
        // System.out.println("load "+size+" in chunks of "+readChunkSize);
        try {
            // alloc initial buffer size.
            data = new char[size];
            // read all the data in chunks of readChunkSize
            int numRead = 0;
            int p = 0;
            do {
                if (p + readChunkSize > data.length) { // overflow?
                    // System.out.println("### overflow p="+p+", data.length="+data.length);
                    data = Arrays.copyOf(data, data.length * 2);
                }
                numRead = r.read(data, p, readChunkSize);
                // System.out.println("read "+numRead+" chars; p was "+p+" is now "+(p+numRead));
                p += numRead;
            } while (numRead != -1); // while not EOF
            // set the actual size of the data available;
            // EOF subtracted one above in p+=numRead; add one back
            n = p + 1;
            //System.out.println("n="+n);
        } finally {
//            r.close();
        }
    }

}
