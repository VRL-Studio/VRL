package my.testpackage;
public class MainWithChainedInvocations {

    
    public my.testpackage.MainWithChainedInvocations m1() {
        return this;
    }
    public static void main(String[] args) {
        my.testpackage.MainWithChainedInvocations m;
        m = (new my.testpackage.MainWithChainedInvocations());
        m.m1().m1();
    }
}
// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:m1:inv:return</string>
    <layout>
      <x>324.3620700617288</x>
      <y>57.24036530501096</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:main:inv:declare m</string>
    <layout>
      <x>266.8778751141102</x>
      <y>1.0848694110329684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>521.0</width>
      <height>383.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:main:inv:declare args</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:m1</string>
    <layout>
      <x>45.0</x>
      <y>181.0</y>
      <width>309.3505859375</width>
      <height>180.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:main:inv:&lt;init&gt;</string>
    <layout>
      <x>2.159789134845204</x>
      <y>215.4099405489691</y>
      <width>493.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:main:inv:m1:0</string>
    <layout>
      <x>667.8955288263747</x>
      <y>413.66270958709157</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:main</string>
    <layout>
      <x>474.5799554865798</x>
      <y>48.649098636429905</y>
      <width>323.3515625</width>
      <height>204.25137329101562</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:main:inv:m1</string>
    <layout>
      <x>663.8166713382085</x>
      <y>222.65733992622984</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>480.8603515625</width>
      <height>230.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:inv:declare this</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainWithChainedInvocations:main:inv:op ASSIGN</string>
    <layout>
      <x>663.852119072537</x>
      <y>55.634031193006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>