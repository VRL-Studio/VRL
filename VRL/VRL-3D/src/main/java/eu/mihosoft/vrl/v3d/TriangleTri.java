/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.v3d;

class TriangleTri{
  
  public double[] x;
  public double[] y;
  
  public TriangleTri(double x1, double y1, double x2, double y2, double x3, double y3){
    this();
    double dx1 = x2-x1;
    double dx2 = x3-x1;
    double dy1 = y2-y1;
    double dy2 = y3-y1;
    double cross = dx1*dy2-dx2*dy1;
    boolean ccw = (cross>0);
    if (ccw){
      x[0] = x1; x[1] = x2; x[2] = x3;
      y[0] = y1; y[1] = y2; y[2] = y3;
    } else{
      x[0] = x1; x[1] = x3; x[2] = x2;
      y[0] = y1; y[1] = y3; y[2] = y2;
    }
  }
  
  public TriangleTri(){
    x = new double[3];
    y = new double[3];
  }

  
  public boolean isInside(double _x, double _y){
    double vx2 = _x-x[0]; double vy2 = _y-y[0];
    double vx1 = x[1]-x[0]; double vy1 = y[1]-y[0];
    double vx0 = x[2]-x[0]; double vy0 = y[2]-y[0];
    
    double dot00 = vx0*vx0+vy0*vy0;
    double dot01 = vx0*vx1+vy0*vy1;
    double dot02 = vx0*vx2+vy0*vy2;
    double dot11 = vx1*vx1+vy1*vy1;
    double dot12 = vx1*vx2+vy1*vy2;
    double invDenom = 1.0 / (dot00*dot11 - dot01*dot01);
    double u = (dot11*dot02 - dot01*dot12)*invDenom;
    double v = (dot00*dot12 - dot01*dot02)*invDenom;
    
    return ((u>0)&&(v>0)&&(u+v<1));    
  }
  
}
