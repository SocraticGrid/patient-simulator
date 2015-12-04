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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

import org.jcpsim.plot.PlotNode;

public class FText extends PNode {
  
  static final long serialVersionUID = 0L;
  
  private String  text;
  private Font    font;
  private Color   color;
  private double  pos;
  private boolean rotate;
  private PNode   node;
  
  private boolean debug = false;
  
  /*
   * Formatted text.
   * @param pos    0.0=left; 0.5=center, 1.0=right
   */
  public FText(String text, Font font, Color color, double pos, boolean rotate, PNode node) {
    this.text   = text;
    this.font   = font;
    this.color  = color;
    this.pos    = pos;
    this.rotate = rotate;
    this.node   = node;
    node.addChild(this);
  }
  
  public void paint(PPaintContext aPaintContext) {

//  System.out.println("  " + text);

    Graphics2D g2 = aPaintContext.getGraphics();
    if (debug) {
      g2.setPaint(new Color(255,0,0,50));
      g2.draw(getBounds());
    }
    g2.setPaint(color);
    TextLayout layout = new TextLayout(text, font, g2.getFontRenderContext()); 
    Rectangle2D r2d = layout.getBounds();
    AffineTransform saveAT = g2.getTransform();
    
    double m00, m10, m01, m11, m02, m12;
//  x' =  m00x + m01y + m02                  y' =  m10x + m11y + m12 
    if (rotate) {
      m00 = 0;
      m10 = -1;
      m01 = 1;
      m11 = 0;
      m02 = getX()+(getWidth()+layout.getAscent()-layout.getDescent())/2.0;  // vertically centered
      m12 = getY()+getHeight()-pos*(getHeight()-r2d.getWidth());
    } else {
      m00 = (getWidth() < r2d.getWidth()+8) ?  (float)(getWidth() / (r2d.getWidth()+8)) : 1.0f;
      m10 = 0;
      m01 = 0;
      m11 = 1;
      m02 = getX()+pos*(getWidth()-r2d.getWidth()); 
      m12 = getY()+(getHeight()+layout.getAscent()-layout.getDescent())/2.0;  // vertically centered
    }
    g2.transform(new AffineTransform(m00, m10, m01, m11, m02, m12));
    layout.draw(g2, 0, 0);
    if (debug) {
      g2.setColor(new Color(128,128,128,128));
      g2.draw(r2d);
    }
    g2.setTransform(saveAT);
  }
}

//-----------------------------------------------------------------------------------
