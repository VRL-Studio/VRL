package my.testpackage;

import eu.mihosoft.vrl.v3d.jcsg.CSG;
import eu.mihosoft.vrl.v3d.jcsg.Transform;
import eu.mihosoft.vrl.v3d.jcsg.Sphere;
import java.nio.file.Paths;
import eu.mihosoft.vrl.v3d.jcsg.Cube;
import eu.mihosoft.vrl.v3d.jcsg.FileUtil;

public class MainCSG {

    
    public static void main(String[] args) {
        // we use cube and sphere as base geometries
        CSG cube;
        cube = (new Cube(2).toCSG());
        CSG sphere;
        sphere = (new Sphere(1.25).toCSG());
        // perform union, difference and intersection
        CSG cubePlusSphere;
        cubePlusSphere = (cube.union(sphere));
        CSG cubeMinusSphere;
        cubeMinusSphere = (cube.difference(sphere));
        CSG cubeIntersectSphere;
        cubeIntersectSphere = (cube.intersect(sphere));
        // translate geometries to prevent overlapping
        CSG union;
        union = (cube.union(sphere.transformed(Transform.unity().translateX(3))).union(cubePlusSphere.transformed(Transform.unity().translateX(6))).union(cubeMinusSphere.transformed(Transform.unity().translateX(9))).union(cubeIntersectSphere.transformed(Transform.unity().translateX(12))));
        // save union as stl
        FileUtil.write(Paths.get("sample.stl"), union.toStlString());
    }
}
// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare sphere</string>
    <layout>
      <x>1271.9614356899592</x>
      <y>466.3052122264901</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:&lt;init&gt;</string>
    <layout>
      <x>300.72468654550164</x>
      <y>215.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:unity:2</string>
    <layout>
      <x>6861.905204823637</x>
      <y>2216.8440160427226</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:unity:0</string>
    <layout>
      <x>5272.14590367359</x>
      <y>1591.221955010212</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:transformed</string>
    <layout>
      <x>4625.685370842682</x>
      <y>1575.518460285575</y>
      <width>251.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:unity:1</string>
    <layout>
      <x>5909.667085135734</x>
      <y>2026.478821702485</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:get</string>
    <layout>
      <x>7517.154020570299</x>
      <y>2769.7845083628445</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare cube</string>
    <layout>
      <x>1.4079085464911145</x>
      <y>230.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN</string>
    <layout>
      <x>664.727628376884</x>
      <y>479.5088224414802</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:translateX:0</string>
    <layout>
      <x>5253.8251598281795</x>
      <y>1800.6018846720442</y>
      <width>217.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:translateX:2</string>
    <layout>
      <x>7141.618990987181</x>
      <y>2224.6138434361537</y>
      <width>217.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:translateX:1</string>
    <layout>
      <x>6258.448610600442</x>
      <y>2038.1379859937933</y>
      <width>217.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare cubeIntersectSphere</string>
    <layout>
      <x>3677.920518026508</x>
      <y>997.2291384131582</y>
      <width>318.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:union</string>
    <layout>
      <x>2064.640323224972</x>
      <y>774.3175799543807</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:translateX</string>
    <layout>
      <x>4336.367357537677</x>
      <y>1588.2051286573649</y>
      <width>217.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare cubeMinusSphere</string>
    <layout>
      <x>2454.1853765278925</x>
      <y>982.6788875350124</y>
      <width>291.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:intersect</string>
    <layout>
      <x>4118.453067408057</x>
      <y>986.9041567870282</y>
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
      <width>822.0</width>
      <height>611.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:&lt;init&gt;:0</string>
    <layout>
      <x>1281.0206229760736</x>
      <y>670.1369261640648</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:toCSG:0</string>
    <layout>
      <x>1281.0206229760736</x>
      <y>905.6757956030399</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:write</string>
    <layout>
      <x>8054.154020570299</x>
      <y>2766.7845083628445</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:union:0</string>
    <layout>
      <x>4973.779503905478</x>
      <y>1588.6047058894394</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:transformed:0</string>
    <layout>
      <x>5233.711738898412</x>
      <y>1977.3909141936626</y>
      <width>334.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare union</string>
    <layout>
      <x>4078.161553452205</x>
      <y>1398.260629100236</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:union:1</string>
    <layout>
      <x>5625.903207016832</x>
      <y>2026.478821702485</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:transformed:1</string>
    <layout>
      <x>6209.239703775374</x>
      <y>2196.124476326904</y>
      <width>350.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:union:2</string>
    <layout>
      <x>6589.961246053528</x>
      <y>2214.254073578245</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:transformed:2</string>
    <layout>
      <x>7466.050147521464</x>
      <y>2209.1000823682634</y>
      <width>377.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:toStlString</string>
    <layout>
      <x>7741.154020570299</x>
      <y>2770.7845083628445</y>
      <width>286.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:union:3</string>
    <layout>
      <x>7527.801733756918</x>
      <y>2403.8550851108503</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:inv:declare this</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>218.171875</width>
      <height>141.79764060370294</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:unity</string>
    <layout>
      <x>4045.5148425908237</x>
      <y>1600.0766598796852</y>
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
      <width>231.22121620178223</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:0</string>
    <layout>
      <x>1643.3881144206507</x>
      <y>692.7848943793507</y>
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
      <width>716.0</width>
      <height>270.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:1</string>
    <layout>
      <x>2046.521948652743</x>
      <y>1000.7972621072414</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:2</string>
    <layout>
      <x>3402.5876746630393</x>
      <y>986.9041567870282</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:toCSG</string>
    <layout>
      <x>287.8830755450069</x>
      <y>385.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:3</string>
    <layout>
      <x>4110.808264313587</x>
      <y>1202.3803639319465</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:declare cubePlusSphere</string>
    <layout>
      <x>1598.0921779900787</x>
      <y>923.7941701752687</y>
      <width>275.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:op ASSIGN:4</string>
    <layout>
      <x>7527.801733756918</x>
      <y>2585.943095805138</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main</string>
    <layout>
      <x>261.14714216922295</x>
      <y>13.28720787207872</y>
      <width>1106.1464339687002</width>
      <height>407.2158788016046</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:my.testpackage.MainCSG:main:inv:difference</string>
    <layout>
      <x>2961.4998645502997</x>
      <y>1037.034011251699</y>
      <width>213.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>