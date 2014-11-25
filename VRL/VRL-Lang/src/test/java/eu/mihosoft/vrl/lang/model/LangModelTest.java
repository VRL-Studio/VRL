/* 
 * LangModelTest.java
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
package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.base.IOUtil;
import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import groovy.lang.GroovyClassLoader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class LangModelTest {

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
    public void codeToModelToCodeTest() {

        createLangModelCompileAndCompareTest("ModelCode01.groovy");
        createLangModelCompileAndCompareTest("ArrayElementCode01.groovy");
        createLangModelCompileAndCompareTest("ArrayElementCode02.groovy");
        createLangModelCompileAndCompareTest("IfCode01.groovy");
        createLangModelCompileAndCompareTest("IfCode02.groovy");

    }

    private void createLangModelCompileAndCompareTest(String fileName) {

        UIBinding.scopes.clear();

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
        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {
                if (s instanceof CompilationUnitDeclaration) {
                    newCode = Scope2Code.getCode((CompilationUnitDeclaration) s);
                    break;
                }
            }
        }

        System.out.println("---- old code ----");
        System.out.println(newCode);

        // checking whether new code compiles
        successCompile = false;
        UIBinding.scopes.clear();
        try {
            CompilerConfiguration cfg = new CompilerConfiguration();
            cfg.addCompilationCustomizers(new ASTTransformationCustomizer(
                    new VRLVisualizationTransformation()));
            GroovyClassLoader gcl = new GroovyClassLoader(LangModelTest.class.getClassLoader(), cfg);
            gcl.parseClass(newCode, "MyFileClass.groovy");
            successCompile = true;
        } catch (Exception ex) {
            Logger.getLogger(LangModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertTrue(fileName + ": " + "Sample code generated from model must compile", successCompile);

        String newNewCode = "";
        // checking whether code from new model is identical to new code
        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {

                System.out.println("scope: " + s);

                if (s instanceof CompilationUnitDeclaration) {
                    newNewCode = Scope2Code.getCode((CompilationUnitDeclaration) s);
                    break;
                }
            }
        }

        System.out.println("---- new code ----");
        System.out.println(newNewCode);

        Assert.assertTrue(fileName + ": " + "Code strings must be identical", newCode.equals(newNewCode));
    }

}
