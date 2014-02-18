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
@eu.mihosoft.vrl.instrumentation.VRLVisualization
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
@eu.mihosoft.vrl.instrumentation.VRLVisualization
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
@eu.mihosoft.vrl.instrumentation.VRLVisualization
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

class MyNewClass {
    
}

// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!>
<map>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m3</string>
    <layout>
      <x>575.110082554268</x>
      <y>50.74500728420012</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m3:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m3:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:abc1234</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m2:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m1:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:abc123</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:abc123</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m2:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m3:inv:m3</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:abc</string>
    <layout>
      <x>464.7197420071651</x>
      <y>420.87825691214954</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:abc123</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:abc</string>
    <layout>
      <x>423.37108952951377</x>
      <y>448.52184732334626</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m3:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>550.0</width>
      <height>800.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m2:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m2:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m3</string>
    <layout>
      <x>262.5653535365203</x>
      <y>397.21425278601794</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m1:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m3:inv:m3</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m3:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:abc</string>
    <layout>
      <x>214.83530372251667</x>
      <y>443.99296102653443</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m3</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m2:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:abc1234</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4</string>
    <layout>
      <x>1138.9882463152273</x>
      <y>1327.2243990676038</y>
      <width>550.0</width>
      <height>800.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3</string>
    <layout>
      <x>200.41220980676886</x>
      <y>999.2775461198613</y>
      <width>550.0</width>
      <height>800.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m2:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m1:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m3:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m3:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:m3:inv:m3</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>550.0</width>
      <height>800.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2:m3</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass2</string>
    <layout>
      <x>918.4995910872439</x>
      <y>275.54987732617315</y>
      <width>550.0</width>
      <height>800.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m1:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m3:inv:m3</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m3:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m3:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m2:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass3:m2:inv:m2</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass4:abc1234</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
    </layout>
  </entry>
</map>
*/
// </editor-fold>