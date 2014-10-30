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

        String code = getResourceAsString("ModelCode01.groovy");

        // checking whether sample code compiles and generate model
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
        Assert.assertTrue("Sample code generated from model must compile", successCompile);

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

        Assert.assertTrue("Code strings must be identical", newCode.equals(newNewCode));

    }

    @Test
    public void classObjectTypeSourceTest() {

        String className = "TestCls01";

        String code = "package my.packagetest01;\n"
                + "public final class TestCls01 extends C1 implements I1,I2 {\n"
                + "  private void m1(Integer a, String b) {}\n"
                + "  public Integer m2() { return 1;}\n"
                + "  private int a = 0;\n"
                + "  private String text = \"text\";\n"
                + "}\n"
                + "public interface I1 {}\n"
                + "public interface I2 {}\n"
                + "public class C1 {}\n"
                + "";

        GroovyClassLoader gcl = new GroovyClassLoader();
        Class<?> cls = gcl.parseClass(code);

        VisualCodeBuilder vBuilder = new VisualCodeBuilder_Impl();

        CompilationUnitDeclaration cuDecl = vBuilder.declareCompilationUnit(
                "TestCls01.groovy", "my.packagetest01");

        ClassObjectTypeSource clsSource = new ClassObjectTypeSource();
        ClassDeclaration cDecl = clsSource.requestModel(cuDecl, cls).get();

        assertTrue("Class name must be \"" + className + "\"",
                cDecl.getClassType().getFullClassName().equals(
                        "my.packagetest01." + className));

        boolean isPublic = cDecl.getClassModifiers().is(Modifier.PUBLIC);
        boolean isFinal = cDecl.getClassModifiers().is(Modifier.FINAL);
        boolean isPrivate = cDecl.getClassModifiers().is(Modifier.PRIVATE);
        boolean isStatic = cDecl.getClassModifiers().is(Modifier.STATIC);

        assertTrue("Class must be public", isPublic);
        assertTrue("Class must be final", isFinal);
        assertFalse("Class must not be private", isPrivate);
        assertFalse("Class must not be static", isStatic);

        boolean extendsOneClass = cDecl.getExtends().getTypes().size() == 1;

        assertTrue("Class must extend one class but extends " + cDecl.getExtends().getTypes().size(), extendsOneClass);

        boolean extendsC1 = cDecl.getExtends().getTypes().get(0).equals(new Type("my.packagetest01.C1"));

        assertTrue("Class must extend C1", extendsC1);

        boolean implementsTwoInterfaces = cDecl.getImplements().getTypes().size() == 2;

        assertTrue("Class must implement two interfaces but implements " + cDecl.getImplements().getTypes().size(), implementsTwoInterfaces);

        boolean implementsI1 = cDecl.getImplements().getTypes().contains(new Type("my.packagetest01.I1"));

        assertTrue("Class must implement I1", implementsI1);

        boolean implementsI2 = cDecl.getImplements().getTypes().contains(new Type("my.packagetest01.I2"));

        assertTrue("Class must implement I2", implementsI2);

        // the number of fields that are automatically declared by groovy
        int numberOFGroovyDefinedFields = 5;

        assertTrue("Class must contain 3 fields, but contains "
                + (cDecl.getVariables().size() - numberOFGroovyDefinedFields),
                cDecl.getVariables().size() - numberOFGroovyDefinedFields == 3);

        Optional<MethodDeclaration> m1Result = cDecl.getDeclaredMethods().
                stream().filter(m -> m.getName().equals("m1")).findFirst();

        assertTrue("Class must contain method m1() ", m1Result.isPresent());

        Optional<MethodDeclaration> m2Result = cDecl.getDeclaredMethods().
                stream().filter(m -> m.getName().equals("m2")).findFirst();

        assertTrue("Class must contain method m2() ", m2Result.isPresent());

        MethodDeclaration m1 = m1Result.get();
        int m1NumParams = m1.getParameters().getParamenters().size();

        assertTrue("m1() must have 2 parameters", m1NumParams == 2);

        MethodDeclaration m2 = m2Result.get();
        int m2NumParams = m2.getParameters().getParamenters().size();

        assertTrue("m2() must have 0 parameters", m2NumParams == 0);

        boolean m1FirstTypeMatches = m1.getParameters().getParamenters().get(0).getType().equals(new Type("java.lang.Integer"));

        assertTrue("First param of m1() must be java.lang.Integer", m1FirstTypeMatches);

        boolean m1SecondTypeMatches = m1.getParameters().getParamenters().get(1).getType().equals(Type.STRING);

        assertTrue("Second param of m1() must be java.lang.String", m1SecondTypeMatches);

        boolean m2ReturnTypeMatches = m2.getReturnType().equals(new Type("java.lang.Integer"));

        assertTrue("Return type of m2() must be java.lang.Integer", m2ReturnTypeMatches);

    }

}
