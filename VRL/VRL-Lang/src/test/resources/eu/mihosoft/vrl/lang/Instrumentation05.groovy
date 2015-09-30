/* global comment*/

package my.testpackage;

/**
 * class MyFileClass
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    private MyFileClass m1(int a) {
        println(a)
        return this;
    }
    
    private MyFileClass m2() {
        return new MyFileClass().m1(1).m1(2);
    }
    
    public static void main(String[] args) {
        new MyFileClass().m2();
    }
}