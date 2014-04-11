/*
 * jCpSim - Java CardioPulmonary SIMulations (http://www.jcpsim.org)
 *
 * Copyright (C) 2002-@year@ Dr. Frank Fischer <frank@jcpsim.org>
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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