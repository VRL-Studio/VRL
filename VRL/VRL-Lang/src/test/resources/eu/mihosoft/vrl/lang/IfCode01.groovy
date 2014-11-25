/* global comment*/

package my.testpackage;

/**
 * class MyFileClass
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    public static void main(String[] args) {
        
        int a = 1;
        int b = 2;
        
        if (a<b) {
            println("a<b");
        }
    }
}