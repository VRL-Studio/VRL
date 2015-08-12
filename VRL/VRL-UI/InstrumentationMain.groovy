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
    public static eu.mihosoft.vrl.v3d.jcsg.CSG createDiff(double r1, double r2) {
        eu.mihosoft.vrl.v3d.jcsg.CSG cube;
        cube = (createCube(r1));
        eu.mihosoft.vrl.v3d.jcsg.CSG sphere;
        sphere = (createSphere(r2));
        return cube.difference(sphere);
    }
    public static void main(String[] args) {
        eu.mihosoft.vrl.v3d.jcsg.CSG res1;
        res1 = (createDiff(13, 10));
        eu.mihosoft.vrl.v3d.jcsg.CSG res2;
        res2 = (createDiff(13, 9));
        eu.mihosoft.vrl.v3d.jcsg.CSG res3;
        res3 = (createDiff(13, 8));
        res3 = (createDiff(13, 7));
    }
}
// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
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
    <string>Script:my.testpackage.MainCSG:createDiff:inv:op ASSIGN</string>
    <layout>
      <x>778.6583681208228</x>
      <y>282.9607836665687</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createCube</string>
    <layout>
      <x>50.056397579767946</x>
      <y>492.77519139764064</y>
      <width>793.6768012893114</width>
      <height>486.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createDiff:inv:declare sphere</string>
    <layout>
      <x>1080.516856242133</x>
      <y>161.68077459428378</y>
      <width>233.59146118164062</width>
      <height>151.23339299368985</height>
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
    <string>Script:my.testpackage.MainCSG:createDiff:inv:difference</string>
    <layout>
      <x>1403.399693446297</x>
      <y>799.1509080868534</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN</string>
    <layout>
      <x>566.4963161067597</x>
      <y>1.6689560767845872</y>
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
      <width>432.4515142299233</width>
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
      <height>882.5198863636363</height>
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
    <string>Script:my.testpackage.MainCSG:main:inv:declare res3</string>
    <layout>
      <x>1116.5568802350374</x>
      <y>467.0322867143526</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createDiff:inv:return</string>
    <layout>
      <x>1834.454720192043</x>
      <y>840.6634451049031</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare res1</string>
    <layout>
      <x>256.460066901611</x>
      <y>31.048915458907167</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createDiff:inv:declare cube</string>
    <layout>
      <x>306.3069870496926</x>
      <y>62.77689172613738</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare res2</string>
    <layout>
      <x>808.4949472405247</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createDiff:inv:op ASSIGN:0</string>
    <layout>
      <x>1104.5081167036678</x>
      <y>779.3899334342689</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:createDiff:0</string>
    <layout>
      <x>551.625462306822</x>
      <y>205.25132947795328</y>
      <width>493.15194582875756</width>
      <height>295.30203604145663</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:createDiff:1</string>
    <layout>
      <x>0.10650187903385527</x>
      <y>554.5654394940567</y>
      <width>480.57934824687493</width>
      <height>283.80791770063945</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createSphere</string>
    <layout>
      <x>224.58156420000907</x>
      <y>27.231853802680885</y>
      <width>623.1296213580301</width>
      <height>393.99326567216355</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createDiff:inv:createSphere</string>
    <layout>
      <x>929.1160893655617</x>
      <y>536.2494419313799</y>
      <width>488.2100199662136</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:createDiff:2</string>
    <layout>
      <x>548.3455411798705</x>
      <y>559.1066960660496</y>
      <width>497.83413869367007</width>
      <height>289.32230214917354</height>
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
    <string>Script:my.testpackage.MainCSG:createDiff:inv:declare r2</string>
    <layout>
      <x>0.0</x>
      <y>181.57893200268745</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createDiff:inv:declare r1</string>
    <layout>
      <x>0.2909204568268864</x>
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
    <string>Script:my.testpackage.MainCSG:createDiff</string>
    <layout>
      <x>895.2632897885192</x>
      <y>528.0275596552435</y>
      <width>690.5866827119339</width>
      <height>416.33920129170326</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare args</string>
    <layout>
      <x>31.88528579575292</x>
      <y>29.782459382122575</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:0</string>
    <layout>
      <x>1118.2005714456734</x>
      <y>290.1396073605182</y>
      <width>200.0</width>
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
      <height>797.9176169092005</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:createDiff</string>
    <layout>
      <x>0.0</x>
      <y>208.8924407522298</y>
      <width>480.15421239841635</width>
      <height>291.66092328613877</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:1</string>
    <layout>
      <x>139.9622118382441</x>
      <y>916.4784169059851</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:2</string>
    <layout>
      <x>1583.116561902281</x>
      <y>1148.8623318074738</y>
      <width>200.0</width>
      <height>150.0</height>
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
      <x>884.7068097898517</x>
      <y>35.066389916636055</y>
      <width>819.9814953017315</width>
      <height>463.93939361829143</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:createDiff:inv:createCube</string>
    <layout>
      <x>302.2877135424976</x>
      <y>289.52871155344764</y>
      <width>383.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>