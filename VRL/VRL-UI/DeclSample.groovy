
@eu.mihosoft.vrl.instrumentation.VRLVisualization
public class MyFileClass {
    
    public int m(int v1) {
        
        // test comment 1
        Integer b = m(v1);
        
        m(v1);
        m(v1);
        m(v1);
        for(int i = 0; i <= 3; i++) {
            // test comment 2
            println("i: ");
        }
    }
}
// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m:inv:m:0</string>
    <layout>
      <x>191.58226494813263</x>
      <y>348.4705197292798</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m:inv:m:1</string>
    <layout>
      <x>137.98343119024528</x>
      <y>504.11290358483706</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>300.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>1140.5265200517463</width>
      <height>800.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m:for:var=i</string>
    <layout>
      <x>610.4532512974235</x>
      <y>607.1369888223586</y>
      <width>400.0</width>
      <height>145.8194111843684</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m:inv:m</string>
    <layout>
      <x>0.0</x>
      <y>204.60341883409086</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>957.4600044355767</width>
      <height>800.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m:for:var=i:inv:println</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m:for:var=i:0</string>
    <layout>
      <x>610.4532512974235</x>
      <y>607.1369888223586</y>
      <width>400.0</width>
      <height>145.8194111843684</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MyFileClass:m:inv:declare b</string>
    <layout>
      <x>2.1530798364386317</x>
      <y>0.0</y>
      <width>400.0</width>
      <height>100.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>