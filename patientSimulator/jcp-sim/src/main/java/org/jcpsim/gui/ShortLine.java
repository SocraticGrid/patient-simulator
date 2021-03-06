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
package org.jcpsim.gui;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;


import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;

/** 
 * <b>TraceLine</b> a class for drawing monosegment lines. 
 */
public class ShortLine extends PNode {

  static final long           serialVersionUID = 0L;
  
  private static final PAffineTransform TEMP_TRANSFORM       = new PAffineTransform();
  
  private           Line2D  line;
  private transient Stroke  stroke;
  private           Color   color;

  public ShortLine(double x1, double y1, double x2, double y2, Color color, Stroke stroke) {
    this.line        = new Line2D.Double(x1, y1, x2, y2);
    this.stroke      = stroke;
    this.color       = color;
    updateBoundsFromLine();
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

  /**
   * @return   FALSE. This is not correct but it seems to work and makes line drawing much faster.
   */
  public boolean intersects(Rectangle2D aBounds) {
/*    
    if (super.intersects(aBounds)) {
      if (line.intersects(aBounds)) {
        return true;
      } else if (stroke != null && color != null) {
        return stroke.createStrokedShape(line).intersects(aBounds);
      }
    }
*/    
    return false;
  }
      
  public Rectangle2D getLineBoundsWithStroke() {
    if (stroke != null) {
      return stroke.createStrokedShape(line).getBounds2D();
    } else {
      return line.getBounds2D();
    }
  }
      
  public void updateBoundsFromLine() {
    Rectangle2D b = getLineBoundsWithStroke();
    super.setBounds(b.getX(), b.getY(), b.getWidth(), b.getHeight());
  }
  
  // - - - Painting - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
  
  protected void paint(PPaintContext paintContext) {
    Graphics2D g2 = paintContext.getGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    
    System.out.printf("ShortLine %4.1f %4.1f   %4.1f %4.1f\n", getX(), getY(), getWidth(), getHeight());
    
    if (stroke  != null) g2.setStroke(stroke);
    if (color   != null) g2.setColor(color);
    g2.draw(line);
  } 

  protected void lineChanged() {
    firePropertyChange(PPath.PROPERTY_CODE_PATH, PPath.PROPERTY_PATH, null, line);
    updateBoundsFromLine();
    invalidatePaint();
  }
}