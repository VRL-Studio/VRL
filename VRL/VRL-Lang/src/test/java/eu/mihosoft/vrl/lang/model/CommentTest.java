/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.base.IOUtil;
import eu.mihosoft.vrl.instrumentation.Comment;
import eu.mihosoft.vrl.lang.VCommentParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CommentTest {

    @Test
    public void testRealCode02VComment() {

        boolean success = false;

        List<Comment> comments = new ArrayList<>();

        try {
            comments = VCommentParser.parse(getResourceAsStringReader("RealCode02.txt"));
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(CommentTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("Parser must not throw an exception", success);

//        System.out.println("#comments: " + comments.size());
        Assert.assertTrue("Expected 34 comments, got" + comments.size(), comments.size() == 34);
    }

    public static InputStream getResourceAsStream(String resourceName) {
        return CommentTest.class.getResourceAsStream("/eu/mihosoft/vrl/lang/" + resourceName);
    }

    public static Reader getResourceAsStringReader(String resourceName) {

        InputStream is = CommentTest.class.getResourceAsStream("/eu/mihosoft/vrl/lang/" + resourceName);
        
//        BufferedReader bR = null;
//        try {
//            bR = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            bR.mark(8192 /*defaultCharBufferSize*/);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(CommentTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(CommentTest.class.getName()).log(Level.SEVERE, null, ex);
//        }


        String tmpCode = IOUtil.convertStreamToString(is);

        return new StringReader(tmpCode);

    }

}
