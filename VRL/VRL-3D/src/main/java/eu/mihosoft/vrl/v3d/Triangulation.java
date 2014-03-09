/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

public class Triangulation {
    /*
     * Triangulates a polygon using simple O(N^2) ear-clipping algorithm
     * Returns a Triangle array unless the polygon can't be triangulated,
     * in which case null is returned.  This should only happen if the
     * polygon self-intersects, though it will not _always_ return null
     * for a bad polygon - it is the caller's responsibility to check for
     * self-intersection, and if it doesn't, it should at least check
     * that the return value is non-null before using.  You're warned!
     */

    TriangleTri[] triangulatePolygon(double[] xv, double[] yv, int vNum) {
        if (vNum < 3) {
            throw new IllegalArgumentException("Less than 3 vertices!");
        }

        TriangleTri[] buffer = new TriangleTri[vNum];
        int bufferSize = 0;
        double[] xrem = new double[vNum];
        double[] yrem = new double[vNum];
        for (int i = 0; i < vNum; ++i) {
            xrem[i] = xv[i];
            yrem[i] = yv[i];
        }

        while (vNum > 3) {
            //Find an ear
            int earIndex = -1;
            for (int i = 0; i < vNum; ++i) {
                if (isEar(i, xrem, yrem)) {
                    earIndex = i;
                    break;
                }
            }

    //If we still haven't found an ear, we're screwed.
            //The user did Something Bad, so return null.
            //This will probably crash their program, since
            //they won't bother to check the return value.
            //At this we shall laugh, heartily and with great gusto.
            if (earIndex == -1) {
                throw new RuntimeException("No Ear can be found!");
            }

    //Clip off the ear:
            //  - remove the ear tip from the list
    //Opt note: actually creates a new list, maybe
            //this should be done in-place instead.  A linked
            //list would be even better to avoid array-fu.
            --vNum;
            double[] newx = new double[vNum];
            double[] newy = new double[vNum];
            int currDest = 0;
            for (int i = 0; i < vNum; ++i) {
                if (currDest == earIndex) {
                    ++currDest;
                }
                newx[i] = xrem[currDest];
                newy[i] = yrem[currDest];
                ++currDest;
            }

            //  - add the clipped triangle to the triangle list
            int under = (earIndex == 0) ? (xrem.length - 1) : (earIndex - 1);
            int over = (earIndex == xrem.length - 1) ? 0 : (earIndex + 1);

            TriangleTri toAdd = new TriangleTri(xrem[earIndex], yrem[earIndex], xrem[over], yrem[over], xrem[under], yrem[under]);
            buffer[bufferSize] = toAdd;
            ++bufferSize;

            //  - replace the old list with the new one
            xrem = newx;
            yrem = newy;
        }
        TriangleTri toAdd = new TriangleTri(xrem[1], yrem[1], xrem[2], yrem[2], xrem[0], yrem[0]);
        buffer[bufferSize] = toAdd;
        ++bufferSize;

        TriangleTri[] res = new TriangleTri[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            res[i] = buffer[i];
        }
        return res;
    }

    PolygonTri[] polygonizeTriangles(TriangleTri[] triangulated) {
        PolygonTri[] polys;
        int polyIndex = 0;

        if (triangulated == null) {
            return null;
        } else {
            polys = new PolygonTri[triangulated.length];
            boolean[] covered = new boolean[triangulated.length];
            for (int i = 0; i < triangulated.length; i++) {
                covered[i] = false;
            }

            boolean notDone = true;

            while (notDone) {
                int currTri = -1;
                for (int i = 0; i < triangulated.length; i++) {
                    if (covered[i]) {
                        continue;
                    }
                    currTri = i;
                    break;
                }
                if (currTri == -1) {
                    notDone = false;
                } else {
                    PolygonTri poly = new PolygonTri(triangulated[currTri]);
                    covered[currTri] = true;
                    for (int i = 0; i < triangulated.length; i++) {
                        if (covered[i]) {
                            continue;
                        }
                        PolygonTri newP = poly.add(triangulated[i]);
                        if (newP == null) {
                            continue;
                        }
                        if (newP.isConvex()) {
                            poly = newP;
                            covered[i] = true;
                        }
                    }
                    polys[polyIndex] = poly;
                    polyIndex++;
                }
            }
        }
        PolygonTri[] ret = new PolygonTri[polyIndex];
        for (int i = 0; i < polyIndex; i++) {
            ret[i] = polys[i];
        }
        return ret;
    }

//Checks if vertex i is the tip of an ear
    boolean isEar(int i, double[] xv, double[] yv) {
        double dx0, dy0, dx1, dy1;
        dx0 = dy0 = dx1 = dy1 = 0;
        if (i >= xv.length || i < 0 || xv.length < 3) {
            return false;
        }
        int upper = i + 1;
        int lower = i - 1;
        if (i == 0) {
            dx0 = xv[0] - xv[xv.length - 1];
            dy0 = yv[0] - yv[yv.length - 1];
            dx1 = xv[1] - xv[0];
            dy1 = yv[1] - yv[0];
            lower = xv.length - 1;
        } else if (i == xv.length - 1) {
            dx0 = xv[i] - xv[i - 1];
            dy0 = yv[i] - yv[i - 1];
            dx1 = xv[0] - xv[i];
            dy1 = yv[0] - yv[i];
            upper = 0;
        } else {
            dx0 = xv[i] - xv[i - 1];
            dy0 = yv[i] - yv[i - 1];
            dx1 = xv[i + 1] - xv[i];
            dy1 = yv[i + 1] - yv[i];
        }
        double cross = dx0 * dy1 - dx1 * dy0;
        if (cross > 0) {
            return false;
        }
        TriangleTri myTri = new TriangleTri(xv[i], yv[i], xv[upper], yv[upper], xv[lower], yv[lower]);
        for (int j = 0; j < xv.length; ++j) {
            if (j == i || j == lower || j == upper) {
                continue;
            }
            if (myTri.isInside(xv[j], yv[j])) {
                return false;
            }
        }
        return true;
    }

}
