package my.testpackage;

@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    public int m1(int v1) {
        this.m1(v1);
    }
    public int m2(int v1, int v2) {
        this.m1(v1);
        this.m1(v2);
        this.m2(v1, v2);    }

    public int m3(int v1, int v2, int v3) {
        this.m1(v1);
        
        this.m2(v1, v2);
        this.m3(v1, v2, v3);
    }
}

