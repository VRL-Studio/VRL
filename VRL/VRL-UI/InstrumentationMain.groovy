package my.testpackage;
public class MainCSG {

    public static eu.mihosoft.vrl.v3d.jcsg.CSG createSphere(double radius) {
        eu.mihosoft.vrl.v3d.jcsg.Sphere sphere;
        sphere = (new eu.mihosoft.vrl.v3d.jcsg.Sphere(radius));
        return sphere.toCSG();
    }
    public static eu.mihosoft.vrl.v3d.jcsg.CSG createCube(double dimensions) {
        eu.mihosoft.vrl.v3d.jcsg.Cube cube;
        cube = (new eu.mihosoft.vrl.v3d.jcsg.Cube(dimensions));
        return cube.toCSG();
    }
    public static void main(String[] args) {
        eu.mihosoft.vrl.v3d.jcsg.CSG cube;
        cube = (createCube(12));
        eu.mihosoft.vrl.v3d.jcsg.CSG sphere;
        sphere = (createSphere(8));
        cube.difference(sphere);
    }
}
// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare sphere</string>
    <layout>
      <x>1093.6567990778933</x>
      <y>120.79194497278216</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube:inv:toCSG</string>
    <layout>
      <x>484.17687065467305</x>
      <y>368.1251530202544</y>
      <width>453.9258292460979</width>
      <height>325.65816463448596</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere:inv:return</string>
    <layout>
      <x>1060.6754997185637</x>
      <y>637.3930713483509</y>
      <width>327.1356399444985</width>
      <height>160.551381577889</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:createSphere</string>
    <layout>
      <x>876.0799232696</x>
      <y>360.04577973157296</y>
      <width>517.5642044242567</width>
      <height>369.8860082032038</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:createCube</string>
    <layout>
      <x>310.10698283280624</x>
      <y>108.09443401600679</y>
      <width>488.2377051968422</width>
      <height>278.07491704112647</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare cube</string>
    <layout>
      <x>22.503024882923487</x>
      <y>183.23891690380555</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube</string>
    <layout>
      <x>65.07958804862845</x>
      <y>427.67469936591175</y>
      <width>793.6768012893114</width>
      <height>486.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere:inv:op ASSIGN</string>
    <layout>
      <x>733.5939538785191</x>
      <y>181.91965160399423</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN</string>
    <layout>
      <x>845.4707920298395</x>
      <y>128.58871361670563</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube:inv:&lt;init&gt;</string>
    <layout>
      <x>522.3669779124697</x>
      <y>7.735606423317017</y>
      <width>389.83418456635667</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere:inv:&lt;init&gt;</string>
    <layout>
      <x>678.2063186324276</x>
      <y>15.302523784982583</y>
      <width>377.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere:inv:declare sphere</string>
    <layout>
      <x>330.5117886393008</x>
      <y>14.064331431459607</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere:inv:declare radius</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube:inv:op ASSIGN</string>
    <layout>
      <x>642.591372212748</x>
      <y>191.4205035447564</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube:inv:declare dimensions</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>260.76727294921875</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>1222.0</width>
      <height>617.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube:inv:return</string>
    <layout>
      <x>1023.4116451155417</x>
      <y>554.0644782808687</y>
      <width>186.6853826151837</width>
      <height>148.47018343115735</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere</string>
    <layout>
      <x>234.59702451258272</x>
      <y>14.712528411963795</y>
      <width>623.1296213580301</width>
      <height>393.99326567216355</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:inv:declare this</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube:inv:declare cube</string>
    <layout>
      <x>273.78445252407664</x>
      <y>4.53786385399022</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare args</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:0</string>
    <layout>
      <x>1002.0676377505234</x>
      <y>808.0393261603588</y>
      <width>362.3492563296877</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>1197.9276094709583</width>
      <height>557.5420834576643</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere:inv:toCSG</string>
    <layout>
      <x>352.2731800248515</x>
      <y>371.91699112346686</y>
      <width>599.11219227352</width>
      <height>407.7267763945488</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main</string>
    <layout>
      <x>893.6949088188937</x>
      <y>47.842725509509165</y>
      <width>819.9814953017315</width>
      <height>463.93939361829143</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:difference</string>
    <layout>
      <x>1727.3554572117262</x>
      <y>410.110932722536</y>
      <width>568.6279256039497</width>
      <height>403.96433012945477</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>
