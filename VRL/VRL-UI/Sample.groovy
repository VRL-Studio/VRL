package my.testpackage;

@eu.mihosoft.vrl.instrumentation.VRLVisualization


public class MyFileClass {
    
    int value1;
    
    public int m1(int v1) {
        this.m2(this.m1(v1), this.m1(v1));
    }

    public int m2(int v1, int v2) {
      return m2(m1(v1), m1(v2))
    }

    public int m3(int v1, int v2, int v3) {

     

      return m3(m1(v1), m1(v2), m2(m1(v2),m1(v3)))
    }
   
}

public class MyFileClass2 {
	public void myMEthod1() {
            println("hello")
        }
}
