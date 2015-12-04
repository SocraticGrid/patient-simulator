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
import java.awt.geom.RoundRectangle2D;

import org.jcpsim.parameter.Input;
import org.jcpsim.run.Global;


public class InputElement extends FrontpanelInput {
    
  static final long serialVersionUID = 0L;

  public Color givenFgColor   = new Color(108, 147, 224);
  public Color givenBgColor   = new Color(141, 202, 245);
  public Color givenTextColor = new Color(  0,   0,   0);
  public Color defaultColor   = new Color(235, 235,   0);
  public static Font  font           = new Font("Lucida Sans Regular", Font.PLAIN, 14);
  
  
  public InputElement(double x, double y, double w, double h, Input par, InputMode mode, Global global) {
    super(x, y, w, h, par, mode, global);
    this.givenBgColor = global.getMode().getInputBgSliderColor();
    this.givenFgColor = global.getMode().getInputFgSliderColor();
  }
  
    
  public void paintElement(Graphics2D g2, double scale) {
      
    g2.setColor(getColor(givenBgColor));
    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 5, 5));
    
    g2.setColor(getColor(givenFgColor));
    g2.fill(new RoundRectangle2D.Double(getInput().getBarX(w), 0, 
                                        getInput().getBarW(w), h, 5, 5));
    
    g2.setColor(defaultColor);
    double pos = w * getInput().Ratio(getInput().getDefault());
    g2.fill(new RoundRectangle2D.Double(pos-1, 4, 2, h-8, 1, 1));
      
    g2.setColor(getColor(givenTextColor));
    
    rubberBand(" " + getInput().getName(), font, g2, 0+2,   0, w/2-4, h);
    rubberBand(getInput().valueAndUnit(),  font, g2, w/2+2, 0, w/2-4, h);
  }
}