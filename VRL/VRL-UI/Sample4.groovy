package my.testpackage;

@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    public int m1(int v1) {
        this.m1(v1);
    }
    public int m2(int v1, int v2) {
        this.m1(v1);
        this.m1(v2);
        this.m2(v1, v2);
    }
    public int m3(int v1, int v2, int v3) {
        this.m1(v1);
        this.m2(v1, v2);
        this.m3(v1, v2, v3);
    }
}

public class MyFileClass2 {
    
    public int abc123(int a) {
    }
    public int abc1234(int a) {
    }
    public int m1(int v1) {
        this.m1(v1);
    }
    public int m2(int v1, int v2) {
        this.m1(v1);
        this.m1(v2);
        this.m2(v1, v2);
    }
    // my special comment
    public int m3(int v1, int v2, int v3) {
        this.m2(v1, v2);
        this.m3(v1, v2, v3);
        this.m1(v1);
    }
    public int abc(int a) {
    }
}

public class MyFileClass3 {
    
    public int abc123(int a) {
    }
    public int abc1234(int a) {
    }
    public int m1(int v1) {
        this.m1(v1);
    }
    public int m2(int v1, int v2) {
        this.m1(v1);
        this.m1(v2);
        this.m2(v1, v2);
    }
    // my special comment
    public int m3(int v1, int v2, int v3) {
        this.m2(v1, v2);
        this.m3(v1, v2, v3);
        this.m1(v1);
    }
    public int abc(int a) {
    }
}

public class MyFileClass4 {
    
    public int abc123(int a) {
    }
    public int abc1234(int a) {
    }
    public int m1(int v1) {
        this.m1(v1);
    }
    public int m2(int v1, int v2) {
        this.m1(v1);
        this.m1(v2);
        this.m2(v1, v2);
    }
    // my special comment
    public int m3(int v1, int v2, int v3) {
        this.m2(v1, v2);
        this.m3(v1, v2, v3);
        this.m1(v1);
    }
    public int abc(int a) {
    }
}

public class MyFileClass5 {
    
    public int abc123(int a) {
    }
    public int abc1234(int a) {
    }
    public int m1(int v1) {
        this.m1(v1);
    }
    public int m2(int v1, int v2) {
        this.m1(v1);
        this.m1(v2);
        this.m2(v1, v2);
    }
    // my special comment
    public int m3(int v1, int v2, int v3) {
        this.m2(v1, v2);
        this.m3(v1, v2, v3);
        this.m1(v1);
    }
    public int abc(int a) {
    }
}
