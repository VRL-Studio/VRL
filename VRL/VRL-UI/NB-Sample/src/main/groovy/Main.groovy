
public class Main {
    
    public int m1(int p1) {
        m1(1);
        m1(2);
    }
    public int m2() {
    }
}

public class A {
    
    public int m1(int p1) {
        m1(m1(1));
    }
    public int m2() {
        
        int a = 2;
        
        A aObj = new A();
        
        return 0
        
    }
}
// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
  <entry>
    <string>Script:A:m2</string>
    <layout>
      <x>56.249823535606744</x>
      <y>332.81145591900656</y>
      <width>400.0</width>
      <height>300.0</height>
      <contentVisible>false</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script</string>
    <layout>
      <x>5.589400632311936</x>
      <y>0.0</y>
      <width>1450.7323227049023</width>
      <height>1294.9093456787427</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:A:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:Main:m2</string>
    <layout>
      <x>126.67247599730884</x>
      <y>396.7214907608024</y>
      <width>400.0</width>
      <height>300.0</height>
      <contentVisible>false</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:Main:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:A:m1:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:A:m1:inv:m1:0</string>
    <layout>
      <x>122.66608452993668</x>
      <y>211.493249189546</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:Main</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>550.0</width>
      <height>800.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:Main:m1:inv:m1</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:A</string>
    <layout>
      <x>633.9817552916419</x>
      <y>128.2921806347189</y>
      <width>550.0</width>
      <height>800.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:Main:m1:inv:m1:0</string>
    <layout>
      <x>90.22894555450871</x>
      <y>203.4574262503628</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>