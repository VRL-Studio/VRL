/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.base.IOUtil;
import eu.mihosoft.vrl.instrumentation.Scope2Code;
import eu.mihosoft.vrl.instrumentation.UIBinding;
import groovy.lang.GroovyClassLoader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class LangModelTest {

    @Test
    public void codeToModelToCodeTest() {

        String code = getResourceAsString("ModelCode01.groovy");

        boolean successCompile = false;
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            gcl.parseClass(code);
            successCompile = true;

        } catch (Exception ex) {
            Logger.getLogger(LangModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("Sample code must compile", successCompile);

        Assert.assertTrue("UIBindings.scopes must be initialized", UIBinding.scopes != null);
        Assert.assertTrue("UIBindings must contain exactly one scope, got " + UIBinding.scopes.size(), UIBinding.scopes.size() == 1);

        String newCode = "";

        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {
                if (s instanceof CompilationUnitDeclaration) {
                    newCode = Scope2Code.getCode((CompilationUnitDeclaration) s);
                    break;
                }
            }
        }

        successCompile = false;

        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            gcl.parseClass(newCode, "MyFileClass.groovy");
            successCompile = true;

        } catch (Exception ex) {
            Logger.getLogger(LangModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue("Sample code generated from model must compile", successCompile);

    }

    public static InputStream getResourceAsStream(String resourceName) {
        return CommentTest.class.getResourceAsStream("/eu/mihosoft/vrl/lang/" + resourceName);
    }

    public static Reader getResourceAsStringReader(String resourceName) {

        return new StringReader(getResourceAsString(resourceName));
    }

    public static String getResourceAsString(String resourceName) {
        InputStream is = CommentTest.class.getResourceAsStream("/eu/mihosoft/vrl/lang/" + resourceName);
        String tmpCode = IOUtil.convertStreamToString(is);
        return tmpCode;
    }
}
