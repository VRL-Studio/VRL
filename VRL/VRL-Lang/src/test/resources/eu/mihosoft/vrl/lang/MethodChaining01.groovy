
/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MethodChaining01 {
    public MethodChaining01 mym(int a) {
        return this;
    }
    
    public MethodChaining01 mym2(int a) {
        return new MethodChaining01().mym(1).mym(1);  ;
    }
    
    public static void main(String[] args) {
        new MethodChaining01().mym(1).mym2(2);  
    }
}

