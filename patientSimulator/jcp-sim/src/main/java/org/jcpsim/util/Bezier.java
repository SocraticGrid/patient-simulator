/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jcpsim.util;

import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;



public class Bezier {
  
  private class Segment {
    
    private double x0, x3, y0;
    private double ax, bx, cx,ay, by, cy;
    private double eqn[];
    private double res[];
    
    
    Segment(double x0, double y0, double x1, double y1, 
            double x2, double y2, double x3, double y3) {
      this.x0 = x0;  
      this.x3 = x3;  
      this.y0 = y0;  
      
      eqn = new double[4];
      res = new double[3];
      
      eqn[1] = 3.0 * (x1 - x0);            // cx
      eqn[2] = 3.0 * (x2 - x1) - eqn[1];   // bx 
      eqn[3] = x3 - x0 - eqn[1] - eqn[2];  // ax
      
      cy = 3.0 * (y1 - y0);
      by = 3.0 * (y2 - y1) - cy;
      ay = y3 - y0 - cy - by;
    }
    
    double  getMinX()          { return x0; }
    double  getMaxX()          { return x3; }
    boolean isWithin(double x) { return  (getMinX() <= x) && (x <= getMaxX()); }
    

    double get(double x) {

      eqn[0] = x0 - x;
      
      int    n = CubicCurve2D.solveCubic(eqn, res);
      double t = 0.0;
      for (int i=0; i<n; i++) {
        if ((0 <= res[i]) && (res[i] <= 1)) {
          t = res[i];  
          break;
        }
      }
      return  ay * t*t*t + by * t*t + cy * t + y0; 
    }
  
    /** t is the parameter value, 0 <= t <= 1 */
    public double getX(double t) {          
     return  (ax * t*t*t) + (bx * t*t) + (cx * t) + x0;
   }
   
    /** t is the parameter value, 0 <= t <= 1 */
    public double getY(double t) {          
      return (ay * t*t*t) + (by * t*t) + (cy * t) + y0;
    }
  }
  
  //---------------------------------------------------------------------
  
  private double xx;
  private double yy;
  private double minX, maxX;
  
  private ArrayList<Segment> segments;
  
  public Bezier(double x, double y) {
    xx   = x;
    yy   = y;
    minX = x;
    maxX = x;
    segments = new ArrayList<Segment>();
  }
  
  public void add(double x1, double y1, double x2, double y2, double x3, double y3) {
    segments.add(new Segment(xx, yy, x1, y1, x2, y2, x3, y3));
    xx   = x3;
    yy   = y3;
    maxX = x3;
  }
  
  /**
   * 
   * @param x   a cycle lasts from 0.0 to 1.0 (and so on)
   * @return    the y value
   */
  public double getY(double x) {
    double xBezier = (x % 1.0) * (getMaxX()-getMinX()) + getMinX();
    for (Segment s:segments) {
      if (s.isWithin(xBezier))  return  s.get(xBezier);
    }
    return 0.0;
  }
  
  
  public double get(double x) {  return getY(x); }
  
  public double getMinX() { return  minX; }
  public double getMaxX() { return  maxX; }
}