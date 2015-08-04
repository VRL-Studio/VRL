/* global comment*/

package my.testpackage;

/**
 * class MyFileClass
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    private MyFileClass m(int a) {
        println("m: " + a);
        return this;
    }
    
    public static void main(String[] args) {
        
        int a = 2+3*2;
        int b = 5-a;
        
        if (b<a) {
            println("a<b: " + (b < a));
        }
        
        int i = 0;
        
        while(i<a) {
//            println("i: " + i);
            i++;
        }
        
        MyFileClass mfc = new MyFileClass();
        
        mfc.m(a).m(b)
    }
}