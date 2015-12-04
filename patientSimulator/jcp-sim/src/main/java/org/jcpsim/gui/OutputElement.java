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