package mypackage;

import eu.mihosoft.vrl.v3d.jcsg.Extrude;
import eu.mihosoft.vrl.v3d.jcsg.samples.ServoHead;
import eu.mihosoft.vrl.v3d.jcsg.CSG;
import eu.mihosoft.vrl.v3d.jcsg.Transform;
import eu.mihosoft.vrl.v3d.jcsg.Cylinder;
import eu.mihosoft.vrl.v3d.jcsg.Vector3d;

public class Main {

    
    public static void main(String[] args) {
        ServoWheel wheel;
        wheel = new ServoWheel();
        wheel.init();
        wheel.toCSG();
    }
}
public class ServoWheel {

    private int toothCount;
    private double minorArmThickness;
    private double minorArmHeight;
    private double toothWidth;
    private double headDiameter;
    private double thickness;
    private double toothLength;
    private double outerRingDepth;
    private double outerWidth;
    private ServoHead servoHead;
    private int numberOfArms;
    private double minorArmLength;
    private double ringThickness;
    private double innerWidth;
    private double outerRingThickness;
    private double headHeight;
    private double headScrewDiameter;
    private double headThickness;
    private double wheelThickness;
    private double toothHeight;
    private double radius;
    
    public void init() {
        toothLength = 0.7;
        toothWidth = 0.1;
        toothHeight = 0.3;
        toothCount = 25;
        headHeight = 4;
        headDiameter = 5.92;
        headScrewDiameter = 2.5;
        headThickness = 1.1;
        servoHead = new ServoHead(toothLength, toothWidth, toothHeight, 
        toothCount, headHeight, headDiameter, headScrewDiameter, headThickness);
        numberOfArms = 3;
        innerWidth = 7;
        outerWidth = 3.5;
        thickness = 2;
        radius = 27.5;
        ringThickness = 3;
        wheelThickness = 5;
        minorArmLength = (radius * 0.75);
        minorArmHeight = headHeight;
        minorArmThickness = 2.5;
        outerRingThickness = ((wheelThickness / 3.0) * 2);
        outerRingDepth = 0.5;
    }
    public CSG toCSG() {
        Double dt;
        dt = (360.0 / numberOfArms);
        CSG arms;
        Integer i;
        i = 0;
        while(i < numberOfArms) {
            CSG arm;
            arm = servoArm(innerWidth, outerWidth, thickness, radius, 
            ringThickness, minorArmThickness, minorArmLength, minorArmHeight).transformed(
            Transform.unity().rotZ(dt * i));
            if (arms == null) {
                arms = arm;
            }
            if (arms != null) {
                arms = arms.union(arm);
            }
            i += 1;
        }
        CSG sHead;
        sHead = servoHead.servoHeadFemale();
        CSG screwHole;
        screwHole = new Cylinder(headScrewDiameter / 2.0, ringThickness * 2, 16).
        toCSG();
        if (arms != null) {
            sHead = sHead.union(arms);
        }
        sHead = sHead.difference(screwHole);
        CSG outerWheelCylinder;
        outerWheelCylinder = new Cylinder(radius, wheelThickness, 64).toCSG();
        CSG innerWheelCylinder;
        innerWheelCylinder = new Cylinder(radius - ringThickness, 
        wheelThickness, 64).toCSG();
        CSG ring;
        ring = outerWheelCylinder.difference(innerWheelCylinder);
        CSG wheel;
        wheel = ring.union(sHead);
        CSG outerRingOutCylinder;
        outerRingOutCylinder = new Cylinder(radius, outerRingThickness, 64).toCSG();
        CSG outerRingInnerCylinder;
        outerRingInnerCylinder = new Cylinder(radius - outerRingDepth, 
        outerRingThickness, 64).toCSG();
        CSG outerRing;
        outerRing = outerRingOutCylinder.difference(outerRingInnerCylinder).transformed(
        Transform.unity().translateZ(
        (wheelThickness * 0.5) - (outerRingThickness * 0.5)));
        wheel = wheel.difference(outerRing);
        return wheel;
    }
    public CSG servoArm(double innerWidth, double outerWidth, double thickness, double radius, double wheelThickness, double minorArmThickness, double minorArmLegth, double minorArmHeight) {
        CSG mainArm;
        mainArm = Extrude.points(Vector3d.z(thickness), 
        new Vector3d((-1 * innerWidth) * 0.5, 0), 
        new Vector3d(innerWidth * 0.5, 0), 
        new Vector3d(outerWidth * 0.5, radius - wheelThickness), 
        new Vector3d((-1 * outerWidth) * 0.5, radius - wheelThickness));
        CSG minorArm;
        minorArm = Extrude.points(Vector3d.z(minorArmThickness), 
        new Vector3d((headDiameter * 0.5) + (headThickness * 0.5), thickness), 
        new Vector3d(
        (minorArmLegth - (headDiameter * 0.5)) - (headThickness * 0.5), 
        thickness), new Vector3d((headDiameter * 0.5) + (headThickness * 0.5), 
        minorArmHeight + (thickness * 0.5))).transformed(
        Transform.unity().rot(-90, 0, 0).translateZ(
        (-1 * minorArmThickness) * 0.5));
        minorArm = minorArm.transformed(Transform.unity().rotZ(-90));
        return mainArm.union(minorArm);
    }
}

// <editor-fold defaultstate="collapsed" desc="VRL-Data">
/*<!VRL!><Type:VRL-Layout>
<map>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare outerWidth</string>
    <layout>
      <x>270.0</x>
      <y>424.18940066024754</y>
      <width>214.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:IF:0</string>
    <layout>
      <x>1617.8923165386002</x>
      <y>1175.2290692239926</y>
      <width>643.2453975374751</width>
      <height>396.5588664374709</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:unity:0</string>
    <layout>
      <x>3444.6492021658714</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:main:inv:declare args</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:difference</string>
    <layout>
      <x>750.0</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:op PLUS_ASSIGN</string>
    <layout>
      <x>2468.0859615886448</x>
      <y>1330.4938877372274</y>
      <width>209.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare innerWidth</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>212.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:IF:inv:op ASSIGN</string>
    <layout>
      <x>311.53664714545823</x>
      <y>185.74675477944348</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op DIV:0</string>
    <layout>
      <x>1789.4133086730644</x>
      <y>510.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare innerWheelCylinder</string>
    <layout>
      <x>2443.201740663729</x>
      <y>911.0887287929684</y>
      <width>282.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:declare arm</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:servoArm</string>
    <layout>
      <x>161.90875</x>
      <y>260.885625</y>
      <width>504.5518733501434</width>
      <height>334.424375</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op NOT_EQUALS</string>
    <layout>
      <x>250.0</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op MINUS:0</string>
    <layout>
      <x>3124.023636736304</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op MINUS:1</string>
    <layout>
      <x>2242.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare toothHeight</string>
    <layout>
      <x>1069.9116022099433</x>
      <y>691.1507266270984</y>
      <width>220.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:op NOT_EQUALS</string>
    <layout>
      <x>1347.5711750051714</x>
      <y>1088.8247582990045</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare outerWidth</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>214.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op TIMES</string>
    <layout>
      <x>4250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:19</string>
    <layout>
      <x>6000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:18</string>
    <layout>
      <x>5750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:17</string>
    <layout>
      <x>5000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare outerRingDepth</string>
    <layout>
      <x>20.0</x>
      <y>424.18940066024754</y>
      <width>253.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:16</string>
    <layout>
      <x>4750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op MINUS:2</string>
    <layout>
      <x>2576.996795108829</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:&lt;init&gt;:4</string>
    <layout>
      <x>2826.996795108829</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:15</string>
    <layout>
      <x>4500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:&lt;init&gt;</string>
    <layout>
      <x>855.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:&lt;init&gt;:3</string>
    <layout>
      <x>1576.9967951088283</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:14</string>
    <layout>
      <x>4000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:&lt;init&gt;:2</string>
    <layout>
      <x>3105.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:13</string>
    <layout>
      <x>3750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:&lt;init&gt;:1</string>
    <layout>
      <x>2105.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:12</string>
    <layout>
      <x>3500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:&lt;init&gt;:0</string>
    <layout>
      <x>1355.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:11</string>
    <layout>
      <x>3250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:10</string>
    <layout>
      <x>3000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op MINUS:0</string>
    <layout>
      <x>2855.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op MINUS:1</string>
    <layout>
      <x>2076.996795108829</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare outerRingInnerCylinder</string>
    <layout>
      <x>2874.023636736304</x>
      <y>1325.4323183990655</y>
      <width>312.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:&lt;init&gt;:5</string>
    <layout>
      <x>4326.996795108828</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:scope</string>
    <layout>
      <x>1625.4716536921992</x>
      <y>737.147116106803</y>
      <width>434.3546853227724</width>
      <height>354.4595336020552</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare minorArmHeight</string>
    <layout>
      <x>750.0</x>
      <y>34.18940066024757</y>
      <width>257.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:&lt;init&gt;:0</string>
    <layout>
      <x>1693.2017406637294</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:&lt;init&gt;:1</string>
    <layout>
      <x>2943.201740663729</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:&lt;init&gt;:2</string>
    <layout>
      <x>2124.023636736304</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:&lt;init&gt;:3</string>
    <layout>
      <x>3374.023636736304</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:points:0</string>
    <layout>
      <x>1444.6492021658698</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare minorArmLegth</string>
    <layout>
      <x>1500.0</x>
      <y>0.0</y>
      <width>250.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:scope:0</string>
    <layout>
      <x>499.99999999999994</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:return</string>
    <layout>
      <x>3742.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare ring</string>
    <layout>
      <x>374.02363673630487</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare headThickness</string>
    <layout>
      <x>569.9116022099442</x>
      <y>691.1507266270984</y>
      <width>247.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare outerWheelCylinder</string>
    <layout>
      <x>1443.2017406637294</x>
      <y>911.0887287929684</y>
      <width>284.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:rot</string>
    <layout>
      <x>1944.6492021658698</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare outerRingOutCylinder</string>
    <layout>
      <x>1874.0236367363038</x>
      <y>1325.4323183990655</y>
      <width>301.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:main:inv:&lt;init&gt;</string>
    <layout>
      <x>500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:union</string>
    <layout>
      <x>1374.0236367363038</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare radius</string>
    <layout>
      <x>1319.9116022099433</x>
      <y>691.1507266270984</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare wheel</string>
    <layout>
      <x>1124.0236367363038</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare toothLength</string>
    <layout>
      <x>1750.0</x>
      <y>34.18940066024757</y>
      <width>223.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare ringThickness</string>
    <layout>
      <x>1270.0</x>
      <y>424.18940066024754</y>
      <width>237.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:IF:inv:union</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel</string>
    <layout>
      <x>126.65331491841708</x>
      <y>856.9410926141001</y>
      <width>1275.296317124441</width>
      <height>598.6337631156814</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init</string>
    <layout>
      <x>3698.7127764005636</x>
      <y>0.0</y>
      <width>545.209012338099</width>
      <height>854.8184822594541</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op DIV</string>
    <layout>
      <x>5250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare servoHead</string>
    <layout>
      <x>520.0</x>
      <y>424.18940066024754</y>
      <width>212.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:main:inv:init</string>
    <layout>
      <x>1000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare innerWidth</string>
    <layout>
      <x>1520.0</x>
      <y>424.18940066024754</y>
      <width>212.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:op EQUALS</string>
    <layout>
      <x>1629.9552564778671</x>
      <y>519.8235723272345</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op MINUS</string>
    <layout>
      <x>1855.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:points</string>
    <layout>
      <x>3355.0</x>
      <y>415.0</y>
      <width>899.0</width>
      <height>494.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:toCSG:1</string>
    <layout>
      <x>3193.201740663729</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:toCSG:0</string>
    <layout>
      <x>1943.2017406637294</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:toCSG:3</string>
    <layout>
      <x>492.5223939525136</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:toCSG:2</string>
    <layout>
      <x>2374.023636736304</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op LESS</string>
    <layout>
      <x>1924.4133086730644</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:IF:inv:union</string>
    <layout>
      <x>16.511620360001935</x>
      <y>247.67430540002903</y>
      <width>519.7756394556382</width>
      <height>340.5309853104559</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare thickness</string>
    <layout>
      <x>1500.0</x>
      <y>34.18940066024757</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare wheelThickness</string>
    <layout>
      <x>1000.0</x>
      <y>0.0</y>
      <width>254.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare this</string>
    <layout>
      <x>0.0</x>
      <y>34.18940066024757</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare minorArmThickness</string>
    <layout>
      <x>1250.0</x>
      <y>0.0</y>
      <width>286.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:IF:inv:op ASSIGN</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:return</string>
    <layout>
      <x>4694.649202165871</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare toothWidth</string>
    <layout>
      <x>1000.0</x>
      <y>34.18940066024757</y>
      <width>214.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare numberOfArms</string>
    <layout>
      <x>770.0</x>
      <y>424.18940066024754</y>
      <width>249.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:transformed</string>
    <layout>
      <x>430.5597142586977</x>
      <y>647.2862017241299</y>
      <width>800.3607436115853</width>
      <height>417.9545986044966</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare thickness</string>
    <layout>
      <x>500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:unity</string>
    <layout>
      <x>1694.6492021658698</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare wheelThickness</string>
    <layout>
      <x>819.911602209944</x>
      <y>691.1507266270984</y>
      <width>254.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:z</string>
    <layout>
      <x>105.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN</string>
    <layout>
      <x>924.4133086730646</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op ASSIGN</string>
    <layout>
      <x>76.99679510882862</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:main:inv:op ASSIGN</string>
    <layout>
      <x>750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:13</string>
    <layout>
      <x>2444.6492021658696</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:11</string>
    <layout>
      <x>3826.9967951088297</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:12</string>
    <layout>
      <x>2194.6492021658696</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare outerRing</string>
    <layout>
      <x>992.5223939525149</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare minorArmHeight</string>
    <layout>
      <x>1750.0</x>
      <y>0.0</y>
      <width>257.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>1414.427623074992</width>
      <height>810.4228051178742</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:main:inv:declare wheel</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:difference:1</string>
    <layout>
      <x>1242.5223939525144</x>
      <y>1930.870582219047</y>
      <width>331.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op PLUS:0</string>
    <layout>
      <x>3576.9967951088297</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:difference:2</string>
    <layout>
      <x>3242.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op PLUS:1</string>
    <layout>
      <x>4076.9967951088297</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:scope:0</string>
    <layout>
      <x>1617.8923165386002</x>
      <y>1175.2290692239926</y>
      <width>643.2453975374751</width>
      <height>396.5588664374709</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:10</string>
    <layout>
      <x>3326.996795108829</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:1</string>
    <layout>
      <x>500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:0</string>
    <layout>
      <x>250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:difference:0</string>
    <layout>
      <x>624.0236367363049</x>
      <y>1325.4323183990655</y>
      <width>315.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:3</string>
    <layout>
      <x>1000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:2</string>
    <layout>
      <x>750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:5</string>
    <layout>
      <x>1500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op PLUS</string>
    <layout>
      <x>1326.9967951088283</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:4</string>
    <layout>
      <x>1250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:translateZ</string>
    <layout>
      <x>2492.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:7</string>
    <layout>
      <x>2250.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:6</string>
    <layout>
      <x>1750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:9</string>
    <layout>
      <x>2750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN:8</string>
    <layout>
      <x>2500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:main:inv:toCSG</string>
    <layout>
      <x>873.8640085262319</x>
      <y>205.51175007440938</y>
      <width>453.4504444413283</width>
      <height>335.77567124645304</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:z:0</string>
    <layout>
      <x>576.9967951088288</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:transformed:0</string>
    <layout>
      <x>3944.6492021658714</x>
      <y>1728.1944130600616</y>
      <width>252.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare headDiameter</string>
    <layout>
      <x>1250.0</x>
      <y>34.18940066024757</y>
      <width>238.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare radius</string>
    <layout>
      <x>750.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:rotZ</string>
    <layout>
      <x>1352.5958343857137</x>
      <y>257.91446227638914</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op TIMES</string>
    <layout>
      <x>2039.4133086730644</x>
      <y>510.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:scope</string>
    <layout>
      <x>2174.4133086730644</x>
      <y>0.0</y>
      <width>862.1514100483296</width>
      <height>436.7779911667508</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare toothCount</string>
    <layout>
      <x>245.0</x>
      <y>39.18940066024757</y>
      <width>216.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op DIV</string>
    <layout>
      <x>674.4133086730646</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare mainArm</string>
    <layout>
      <x>2000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:&lt;init&gt;</string>
    <layout>
      <x>2000.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:op ASSIGN</string>
    <layout>
      <x>1379.955256477867</x>
      <y>519.8235723272345</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:op TIMES</string>
    <layout>
      <x>1090.5540362224567</x>
      <y>257.91446227638926</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:transformed</string>
    <layout>
      <x>2944.6492021658714</x>
      <y>1728.1944130600616</y>
      <width>223.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG</string>
    <layout>
      <x>2285.985750001466</x>
      <y>1370.4848754943596</y>
      <width>1351.2197535765404</width>
      <height>734.8446709692914</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:servoHeadFemale</string>
    <layout>
      <x>860.792831455654</x>
      <y>491.77342069210096</y>
      <width>310.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:9</string>
    <layout>
      <x>742.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:8</string>
    <layout>
      <x>2624.023636736304</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:3</string>
    <layout>
      <x>1193.20174066373</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:main</string>
    <layout>
      <x>256.53229577609767</x>
      <y>36.24912875097033</y>
      <width>1086.5686584121208</width>
      <height>640.579855192053</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:2</string>
    <layout>
      <x>0.0</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare minorArmThickness</string>
    <layout>
      <x>500.0</x>
      <y>34.18940066024757</y>
      <width>286.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:1</string>
    <layout>
      <x>1289.4133086730644</x>
      <y>510.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:0</string>
    <layout>
      <x>1674.4133086730644</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:10</string>
    <layout>
      <x>2992.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:7</string>
    <layout>
      <x>1624.0236367363038</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:6</string>
    <layout>
      <x>874.0236367363052</x>
      <y>1325.4323183990655</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:5</string>
    <layout>
      <x>3443.201740663729</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while</string>
    <layout>
      <x>2174.4133086730644</x>
      <y>0.0</y>
      <width>862.1514100483296</width>
      <height>436.7779911667508</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare outerRingThickness</string>
    <layout>
      <x>1770.0</x>
      <y>424.18940066024754</y>
      <width>286.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:4</string>
    <layout>
      <x>2193.201740663729</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:toCSG</string>
    <layout>
      <x>2539.4133086730644</x>
      <y>510.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op ASSIGN:1</string>
    <layout>
      <x>4194.649202165871</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op ASSIGN:0</string>
    <layout>
      <x>3194.6492021658714</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op ASSIGN:11</string>
    <layout>
      <x>3492.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare minorArmLength</string>
    <layout>
      <x>1020.0</x>
      <y>424.18940066024754</y>
      <width>260.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:inv:unity</string>
    <layout>
      <x>778.827826785503</x>
      <y>357.75104362264034</y>
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
      <width>733.0</width>
      <height>737.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:union</string>
    <layout>
      <x>4444.649202165871</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare sHead</string>
    <layout>
      <x>3246.207081565035</x>
      <y>115.204734517566</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:declare minorArm</string>
    <layout>
      <x>326.9967951088287</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare i</string>
    <layout>
      <x>1424.4133086730644</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES</string>
    <layout>
      <x>355.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:IF</string>
    <layout>
      <x>1625.4716536921992</x>
      <y>737.147116106803</y>
      <width>434.3546853227724</width>
      <height>354.4595336020552</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:rotZ</string>
    <layout>
      <x>3694.6492021658714</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op MINUS</string>
    <layout>
      <x>2693.201740663729</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:translateZ</string>
    <layout>
      <x>2694.6492021658714</x>
      <y>1728.1944130600616</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare headScrewDiameter</string>
    <layout>
      <x>319.91160220994476</x>
      <y>691.1507266270984</y>
      <width>290.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op TIMES:0</string>
    <layout>
      <x>5500.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:unity</string>
    <layout>
      <x>1492.5223939525144</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:init:inv:op ASSIGN</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op TIMES:1</string>
    <layout>
      <x>1992.5223939525144</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:&lt;init&gt;</string>
    <layout>
      <x>2289.4133086730644</x>
      <y>510.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:op TIMES:0</string>
    <layout>
      <x>1742.522393952514</x>
      <y>1930.870582219047</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:9</string>
    <layout>
      <x>3076.996795108829</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm</string>
    <layout>
      <x>2285.883636845474</x>
      <y>850.4772074475222</y>
      <width>1144.2061885609796</width>
      <height>479.0338863026057</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:8</string>
    <layout>
      <x>2326.996795108829</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare screwHole</string>
    <layout>
      <x>1539.4133086730644</x>
      <y>510.0</y>
      <width>210.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:inv:declare headHeight</string>
    <layout>
      <x>30.0</x>
      <y>729.1894006602475</y>
      <width>218.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:IF</string>
    <layout>
      <x>499.99999999999994</x>
      <y>911.0887287929684</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:3</string>
    <layout>
      <x>2355.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:2</string>
    <layout>
      <x>1605.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:1</string>
    <layout>
      <x>1105.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:0</string>
    <layout>
      <x>605.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare dt</string>
    <layout>
      <x>424.4133086730646</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:while:IF:inv:op ASSIGN:0</string>
    <layout>
      <x>795.1363339315515</x>
      <y>155.44595934178574</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:7</string>
    <layout>
      <x>1826.9967951088283</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:6</string>
    <layout>
      <x>1076.9967951088283</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:transformed</string>
    <layout>
      <x>2742.522393952514</x>
      <y>1930.870582219047</y>
      <width>254.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:5</string>
    <layout>
      <x>826.9967951088281</x>
      <y>1161.7957510773006</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:servoArm:inv:op TIMES:4</string>
    <layout>
      <x>2605.0</x>
      <y>415.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.Main:inv:declare this</string>
    <layout>
      <x>0.0</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
  <entry>
    <string>Script:mypackage.ServoWheel:toCSG:inv:declare arms</string>
    <layout>
      <x>1174.4133086730646</x>
      <y>0.0</y>
      <width>200.0</width>
      <height>150.0</height>
      <contentVisible>true</contentVisible>
    </layout>
  </entry>
</map>
*/
// </editor-fold>