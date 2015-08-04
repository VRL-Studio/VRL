/* global comment*/

package my.testpackage;

/**
 * class MyFileClass
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    public MyFileClass() {
    }
    
    public MyFileClass(int param1, int param2) {
    }
    
    public static void main(String[] args) {
        
        int a = 2+3*2;
        int b = 5-a;
        
        //MyFileClass mfc = new MyFileClass(a,b+1);
        
        if (a<b) {
            println("a<b: " + (a < b));
        }
    }
}