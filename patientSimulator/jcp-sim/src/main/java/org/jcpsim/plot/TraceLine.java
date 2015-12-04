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
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jcpsim.run.Global;

import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;

/** 
 * Draws monosegment lines. 
 */
public class TraceLine extends TraceShape {  
  
  static final long           serialVersionUID = 0L;
  
  private static final PAffineTransform TEMP_TRANSFORM = new PAffineTransform();
  
  private           Line2D  line;
  private transient Stroke  stroke;

  
  public TraceLine(double x1, double y1, double x2, double y2, 
                     Paint strokePaint, Stroke stroke) {
    this.line        = new Line2D.Double();
    this.stroke      = stroke;
    setPaint(strokePaint);
    set(x1, y1, x2, y2);
  }

  // - - - Stroke - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  /*
   * Set coordinates.
   * @param x1  x coordinate of last position
   * @param y1  x coordinate of last position
   * @param x2  x coordinate of actual position
   * @param y2  x coordinate of actual position
   */
  public void set(double x1, double y1, double x2, double y2) {
    line.setLine(x1, y1, x2, y2);
    Rectangle2D b = getLineBoundsWithStroke();
    super.setBounds(b);
    invalidatePaint();
  }
    
  // - - - Bounds - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

  public boolean setBounds(double x, double y, double width, double height) {
    if (!super.setBounds(x, y, width, height)) {
      return false;
    }

    Rectangle2D lineBounds = line.getBounds2D();
    Rectangle2D lineStrokeBounds = getLineBoundsWithStroke();
    double strokeOutset = Math.max(lineStrokeBounds.getWidth() - lineBounds.getWidth(), 
                                       lineStrokeBounds.getHeight() - lineBounds.getHeight());
    
    x += strokeOutset / 2;
    y += strokeOutset / 2;
    width -= strokeOutset;
    height -= strokeOutset;
    
    TEMP_TRANSFORM.setToIdentity();
    TEMP_TRANSFORM.translate(x, y);
    TEMP_TRANSFORM.scale(width / lineBounds.getWidth(), height / lineBounds.getHeight());
    TEMP_TRANSFORM.translate(-lineBounds.getX(), -lineBounds.getY());   
    TEMP_TRANSFORM.transform(line.getP1(), line.getP1());
    TEMP_TRANSFORM.transform(line.getP2(), line.getP2());
    return true;
  }

  public boolean intersects(Rectangle2D aBounds) {
    if (super.intersects(aBounds)) {
      if (line.intersects(aBounds)) {
        return true;
      } else if (stroke != null && getPaint() != null) {
        return stroke.createStrokedShape(line).intersects(aBounds);
      }
    }
    return false;
  }
      
  public Rectangle2D getLineBoundsWithStroke() {
    if (stroke != null) {
      return stroke.createStrokedShape(line).getBounds2D();
    } else {
      return line.getBounds2D();
    }
  }
      
  // - - - Painting - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  
  protected void paint(PPaintContext paintContext) {
    Graphics2D g2 = paintContext.getGraphics();
    Global.setRenderingQuality(g2);
    if (stroke      != null) g2.setStroke(stroke);
    if (getPaint()  != null) g2.setPaint(getPaint());
    g2.draw(line);
  } 
}
