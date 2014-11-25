/* global comment*/

package my.testpackage;

/**
 * class MyFileClass
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    public static void main(String[] args) {
        
        String s = args[1+2+3];
        
        System.out.println("Hello " + s);
    }
}