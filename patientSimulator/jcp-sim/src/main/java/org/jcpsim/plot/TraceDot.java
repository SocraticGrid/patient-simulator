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
package org.jcpsim.plot;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jcpsim.run.Global;

import edu.umd.cs.piccolo.util.PPaintContext;

/*
 * Draws a dot.
 */
public class TraceDot extends TraceShape {  
  
  static final long           serialVersionUID = 0L;
  
  private Ellipse2D ellipse;
  
  private double x;
  private double y;
  private double diameter;

  
  public TraceDot(double x, double y, double diameter, Paint paint) {
    this.x = x;
    this.y = y;
    this.diameter = diameter;
    setPaint(paint);
    ellipse = new Ellipse2D.Double();
    updateBounds();
    invalidatePaint();
  }

  // - - - Stroke - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  /*
   * Set coordinates.
   * @param x1  ignored
   * @param y1  ignored
   * @param x2  x coordinate of the dot
   * @param y2  x coordinate of the dot
   */
  public void set(double x1, double y1, double x2, double y2) {
    this.x = x2;
    this.y = y2;
    updateBounds();
    invalidatePaint();
  }
    
  // - - - Bounds - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

  public boolean setBounds(double x, double y, double width, double height) {
    if(super.setBounds(x, y, width, height)) {
      ellipse.setFrame(x, y, width, height);
      return true;
    }
    return false;
  }
      
  public void updateBounds() {
    setBounds(x-diameter/2, y-diameter/2, diameter, diameter);
  }
  
  public boolean intersects(Rectangle2D aBounds) {
    return ellipse.intersects(aBounds);
  }
  
    
  public void paint(PPaintContext aPaintContext) {
    Graphics2D g2 = aPaintContext.getGraphics(); 
    Global.setRenderingQuality(g2);
    g2.setPaint(getPaint());
    g2.fill(ellipse);
  }
}