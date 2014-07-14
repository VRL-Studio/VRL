/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InstrumentationTest {

    @Test
    public void testMethodCallInstrumentation() {
        new GroovyShell().parse(
                "@eu.mihosoft.vrl.instrumentation.VRLInstrumentation\n"
                + "public class A {\n"
                + "    \n"
                + "    public void m2(int p1) {\n"
                + "        A.m1(A.m1(1));\n"
                + "    }\n"
                + "    public static int m1(int p1) {\n"
                + "        println(\"p1: \" + (p1+1));\n"
                + "        return p1+1;\n"
                + "    }\n"
                + "    public static void main(String[] args) {"
                + "        A a = new A();"
                + "        a.m2(1);"
                + "    }"
                + "}").run();
    }
}
