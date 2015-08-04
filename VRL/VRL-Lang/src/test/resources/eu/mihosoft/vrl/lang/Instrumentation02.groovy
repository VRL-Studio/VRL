/* global comment*/

package my.testpackage;

/**
 * class MyFileClass
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {

    public static void main(String[] args) {
        int n = 3;
        int i = 0;
        
        while(i<n) {
            println("i: " + i);
            i=i+1;
        }
    }
}