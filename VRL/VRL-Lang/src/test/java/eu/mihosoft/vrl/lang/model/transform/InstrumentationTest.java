/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.base.IOUtil;
import eu.mihosoft.vrl.lang.model.CommentTest;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.LangModelTest;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.UIBinding;
import groovy.lang.GroovyClassLoader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InstrumentationTest {

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

    @Test
    public void instrumentationTest() {
        
        UIBinding.scopes.clear();
        
        String fileName = "Instrumentation02.groovy";
        
        String code = getResourceAsString(fileName);

        // checking whether sample code compiles and generate model
        boolean successCompile = false;
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            gcl.parseClass(code);
            successCompile = true;

        } catch (Exception ex) {
            Logger.getLogger(LangModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertTrue(fileName + ": " + "Sample code must compile", successCompile);
        Assert.assertTrue(fileName + ": " + "UIBindings.scopes must be initialized", UIBinding.scopes != null);
        Assert.assertTrue(fileName + ": " + "UIBindings must contain exactly one scope, got " + UIBinding.scopes.size(), UIBinding.scopes.size() == 1);

        // generating new code from model
        String newCode = "";
        CompilationUnitDeclaration cu = null;
        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {
                if (s instanceof CompilationUnitDeclaration) {
                    
                    cu = (CompilationUnitDeclaration) s;
                    newCode = Scope2Code.getCode(cu);
                    break;
                }
            }
        }
        
        System.out.println(newCode);
        
        InstrumentCode instrumentCode = new InstrumentCode();
        
        CompilationUnitDeclaration newCu = instrumentCode.transform(cu);
        
        String instrumentedCode = Scope2Code.getCode(newCu);
        
        System.out.println(instrumentedCode);
        
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class<?> instrumentedCodeClass = gcl.parseClass(instrumentedCode);
            instrumentedCodeClass.getMethod("main", String[].class).
                    invoke(instrumentedCodeClass, (Object)new String[0]);

        } catch (CompilationFailedException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(LangModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
