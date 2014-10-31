/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import groovy.lang.GroovyClassLoader;
import java.util.Optional;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ClassObjectTypeSourceTest {
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
