/* global comment*/

package my.testpackage;

/**
 * class MyFileClass
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    static int m(int v) {
        return v+v;
    }

    public static void main(String[] args) {
        int n = 3;
        int i = 0;
        
        println("!!!before-while")
        
        while(i<2*3+m(1)) {
            println("i: " + i);
            i=i+1;
        }
    }
}