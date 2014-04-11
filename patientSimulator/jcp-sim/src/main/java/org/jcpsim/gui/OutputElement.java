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
package org.jcpsim.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.*;
import org.jcpsim.parameter.*;
import org.jcpsim.run.Global;

  
public class OutputElement extends FrontpanelElement {
    
  static final long serialVersionUID = 0L;

  public Color textColor       = new Color(  0,   0,   0);
  public Color outputBgColor1  = new Color(200, 200,   0);
  public Color outputBgColor2  = new Color(255, 255, 100);
  public static Font  font            = new Font("Lucida Sans Regular", Font.PLAIN, 14);

  
  Output output;
  
  // --------------------------------------------------------------------------
    
  public OutputElement(double x, double y, double w, double h, Output output, Global global) {
    super(x, y, w, h, global);
    this.output = output;
    this.outputBgColor2 = global.getMode().getOutputBgSliderColor();
    this.outputBgColor1 = global.getMode().getOutputFgSliderColor();
  }

  double displayedValue = Double.NaN;
    
  
  public void macroTimeStep(double t) {
    if (displayedValue != output.get()) {
      displayedValue = output.get();
      repaint();
    }
  }

    
  public void paintElement(Graphics2D g2, double scale) {
      
    g2.setColor(outputBgColor2);
    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 5, 5));
    g2.setColor(outputBgColor1);
    g2.fill(new RoundRectangle2D.Double(output.getBarX(w), 0, 
                                        output.getBarW(w), h, 5, 5));
      
    g2.setColor(textColor);
    rubberBand(" " + output.getName(), font, g2, 0+2,   0, w/2-4, h);
    rubberBand(output.valueAndUnit(),  font, g2, w/2+2, 0, w/2-4, h);
  }
}