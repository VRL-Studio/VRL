/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.v3d;

class PolygonTri{
  
  public double[] x;
  public double[] y;
  public int nVertices;
  
  public PolygonTri(double[] _x, double[] _y){
    nVertices = _x.length;
//    println("length "+nVertices);
    x = new double[nVertices];
    y = new double[nVertices];
    for (int i=0; i<nVertices; ++i){
      x[i] = _x[i];
      y[i] = _y[i];
    }
  }
  
  public PolygonTri(TriangleTri t){
    this(t.x,t.y);
  }
  
  public void set(PolygonTri p){
    nVertices = p.nVertices;
    x = new double[nVertices];
    y = new double[nVertices];
    for (int i=0; i<nVertices; ++i){
      x[i] = p.x[i];
      y[i] = p.y[i];
    }
  }
  
  /*
   * Assuming the polygon is simple, checks
   * if it is convex.
   */
  public boolean isConvex(){
    boolean isPositive = false;
    for (int i=0; i<nVertices; ++i){
      int lower = (i==0)?(nVertices-1):(i-1);
      int middle = i;
      int upper = (i==nVertices-1)?(0):(i+1);
      double dx0 = x[middle]-x[lower];
      double dy0 = y[middle]-y[lower];
      double dx1 = x[upper]-x[middle];
      double dy1 = y[upper]-y[middle];
      double cross = dx0*dy1-dx1*dy0;
      //Cross product should have same sign
      //for each vertex if poly is convex.
      boolean newIsP = (cross>0)?true:false;
      if (i==0){
        isPositive = newIsP;
      } else if (isPositive != newIsP){
        return false;
      }
    }
    return true;
  }
  
  /*
   * Tries to add a triangle to the polygon.
   * Returns null if it can't connect properly.
   * Assumes bitwise equality of join vertices.
   */
  public PolygonTri add(TriangleTri t){
    //First, find vertices that connect
    int firstP = -1; 
    int firstT = -1;
    int secondP = -1; 
    int secondT = -1;
//    println("nVertices: "+this.nVertices);
    for (int i=0; i < nVertices; i++){
      if (t.x[0] == x[i] && t.y[0] == y[i]){
//        println("found p0");
        if (firstP == -1){
          firstP = i; firstT = 0;
        } else{
          secondP = i; secondT = 0;
        }
      } else if (t.x[1] == x[i] && t.y[1] == y[i]){
//        println("found p1");
        if (firstP == -1){
          firstP = i; firstT = 1;
        } else{
          secondP = i; secondT = 1;
        }
      } else if (t.x[2] == x[i] && t.y[2] == y[i]){
//        println("found p2");
        if (firstP == -1){
          firstP = i; firstT = 2;
        } else{
          secondP = i; secondT = 2;
        }
      } else {
//        println(t.x[0]+" "+t.y[0]+" "+t.x[1]+" "+t.y[1]+" "+t.x[2]+" "+t.y[2]);
//        println(x[0]+" "+y[0]+" "+x[1]+" "+y[1]);
      }
    }
    //Fix ordering if first should be last vertex of poly
    if (firstP == 0 && secondP == nVertices-1){
      firstP = nVertices-1;
      secondP = 0;
    }
    
    //Didn't find it
    if (secondP == -1) return null;
    
    //Find tip index on triangle
    int tipT = 0;
    if (tipT == firstT || tipT == secondT) tipT = 1;
    if (tipT == firstT || tipT == secondT) tipT = 2;
    
    double[] newx = new double[nVertices+1];
    double[] newy = new double[nVertices+1];
    int currOut = 0;
    for (int i=0; i<nVertices; i++){
      newx[currOut] = x[i];
      newy[currOut] = y[i];
      if (i == firstP){
        ++currOut;
        newx[currOut] = t.x[tipT];
        newy[currOut] = t.y[tipT];
      }
      ++currOut;
    }
    return new PolygonTri(newx,newy);
  }
  
}
