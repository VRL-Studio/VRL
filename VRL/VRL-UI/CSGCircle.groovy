package mypackage;

import eu.mihosoft.vrl.v3d.jcsg.CSG;
import eu.mihosoft.vrl.v3d.jcsg.Transform;
import eu.mihosoft.vrl.v3d.jcsg.Cube;

public class MyClass {

    
    public static CSG createCube(double w, double h, double d) {
        return new Cube(w, h, d).toCSG();
    }
    public static CSG createCircle(CSG csg, double radius, int n) {
        CSG objects;
        Double step;
        step = (360.0 / n);
        Integer i;
        i = 0;
        while(i < 360) {
            CSG obj;
            obj = csg.transformed(Transform.unity().translate(radius, 0, 0)).transformed(
            Transform.unity().rotZ(i * step));
            if (objects == null) {
                objects = obj;
            }
            if (objects != null) {
                objects = objects.union(obj);
            }
            i += 1;
        }
        return objects;
    }
    public static void main(String[] args) {
        CSG cube;
        cube = createCube(10.0, 10.0, 1.0);
        CSG prot;
        prot = createCube(1.0, 1.0, 1.0);
        cube.difference(createCircle(prot, 4.0, 10));
    }
}

// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
  <entry>
    <string>Script:mypackage.MyClass:createCube:inv:return</string>
    <layout>
      <x>1250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:declare obj</string>
    <layout>
      <x>0.0</x>
      <y>157.09878129188533</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:declare i</string>
    <layout>
      <x>1500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:declare args</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:scope</string>
    <layout>
      <x>1938.2758071316225</x>
      <y>201.9621531259909</y>
      <width>549.5295181625843</width>
      <height>296.8318987660218</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main</string>
    <layout>
      <x>354.95563750084943</x>
      <y>44.918821028589065</y>
      <width>757.8774177969908</width>
      <height>344.4079656206598</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:op NOT_EQUALS</string>
    <layout>
      <x>1863.7562190043268</x>
      <y>954.9505750097564</y>
      <width>250.97764587402344</width>
      <height>189.56878099142637</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:declare prot</string>
    <layout>
      <x>1189.6825963349409</x>
      <y>212.04757874676613</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:op ASSIGN:0</string>
    <layout>
      <x>1528.020670638638</x>
      <y>175.3690117347372</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:op DIV</string>
    <layout>
      <x>1000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:IF</string>
    <layout>
      <x>1291.4011815457065</x>
      <y>915.7425330908444</y>
      <width>480.53312607844737</width>
      <height>304.0303731218046</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:declare n</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:op TIMES</string>
    <layout>
      <x>1609.0252120413306</x>
      <y>16.892261619567876</y>
      <width>200.0</width>
      <height>205.30752851147136</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCube:inv:declare w</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:return</string>
    <layout>
      <x>2500.0</x>
      <y>0.0</y>
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
      <width>1114.0</width>
      <height>680.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCube:inv:toCSG</string>
    <layout>
      <x>1000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:declare step</string>
    <layout>
      <x>750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:IF:inv:op ASSIGN</string>
    <layout>
      <x>335.6060616689985</x>
      <y>118.19840309605163</y>
      <width>213.76117706298828</width>
      <height>169.0584367167271</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:declare csg</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:op PLUS_ASSIGN</string>
    <layout>
      <x>2465.02180922358</x>
      <y>1454.04127941954</y>
      <width>209.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:declare objects</string>
    <layout>
      <x>500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:createCube:0</string>
    <layout>
      <x>980.008313707761</x>
      <y>464.170427271683</y>
      <width>447.01323704004244</width>
      <height>274.25391309504676</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:createCircle</string>
    <layout>
      <x>1522.8853012667028</x>
      <y>405.1887348884517</y>
      <width>496.7883866374609</width>
      <height>376.4348412262808</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:difference</string>
    <layout>
      <x>2112.2834160733705</x>
      <y>404.02235504283004</y>
      <width>385.41460304223165</width>
      <height>322.4447455869537</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:scope</string>
    <layout>
      <x>1291.4011815457065</x>
      <y>915.7425330908444</y>
      <width>480.53312607844737</width>
      <height>304.0303731218046</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:unity</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while</string>
    <layout>
      <x>1938.2758071316225</x>
      <y>201.9621531259909</y>
      <width>549.5295181625843</width>
      <height>296.8318987660218</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:transformed:0</string>
    <layout>
      <x>1106.1042478485094</x>
      <y>399.7469797725398</y>
      <width>480.76519583910977</width>
      <height>312.3146218546015</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCube:inv:declare d</string>
    <layout>
      <x>500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:op EQUALS</string>
    <layout>
      <x>1028.443045751389</x>
      <y>915.7425330908444</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:translate</string>
    <layout>
      <x>500.0</x>
      <y>0.0</y>
      <width>430.56132690066624</width>
      <height>284.5679027366638</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCube:inv:declare h</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCube</string>
    <layout>
      <x>5.833415076215277</x>
      <y>175.0024522864582</y>
      <width>318.5569076538086</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:createCube</string>
    <layout>
      <x>500.0</x>
      <y>0.0</y>
      <width>600.8246783460722</width>
      <height>362.72634118237835</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:op LESS</string>
    <layout>
      <x>2000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:scope:0</string>
    <layout>
      <x>1867.9500412017755</x>
      <y>1189.8608424685171</y>
      <width>530.6154843873576</width>
      <height>386.85266401801334</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:IF:inv:union</string>
    <layout>
      <x>356.78605792887794</x>
      <y>71.22731042639475</y>
      <width>234.0</width>
      <height>268.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:op ASSIGN</string>
    <layout>
      <x>1219.4198680287936</x>
      <y>14.003492591825989</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:op ASSIGN</string>
    <layout>
      <x>1250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>1058.6654224395752</width>
      <height>611.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:declare radius</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:transformed</string>
    <layout>
      <x>978.0221572659667</x>
      <y>3.051421756744385</y>
      <width>200.0</width>
      <height>277.24146490450954</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:IF:inv:op ASSIGN:0</string>
    <layout>
      <x>269.36049504779675</x>
      <y>387.0323395656362</y>
      <width>512.3280581657416</width>
      <height>180.5832888269328</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:main:inv:declare cube</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle</string>
    <layout>
      <x>109.45456490464103</x>
      <y>417.9799979593491</y>
      <width>668.8175029286789</width>
      <height>198.7794409040763</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:inv:op ASSIGN:0</string>
    <layout>
      <x>1750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:unity:0</string>
    <layout>
      <x>1359.0252120413306</x>
      <y>16.892261619567876</y>
      <width>200.0</width>
      <height>254.2280305256315</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:rotZ</string>
    <layout>
      <x>1859.0252120413306</x>
      <y>16.892261619567876</y>
      <width>200.0</width>
      <height>220.59518539089643</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCube:inv:&lt;init&gt;</string>
    <layout>
      <x>750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:inv:declare this</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:inv:op ASSIGN</string>
    <layout>
      <x>778.4430457513896</x>
      <y>915.7425330908444</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.MyClass:createCircle:while:IF:0</string>
    <layout>
      <x>1867.9500412017755</x>
      <y>1189.8608424685171</y>
      <width>530.6154843873576</width>
      <height>386.85266401801334</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>