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

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jcpsim.run.Global;

import edu.umd.cs.piccolox.pswing.PSwing;
import java.util.logging.Logger;

public class Slider extends JSlider {
  
  static final long serialVersionUID = 0L;
  
  public String            name;
  public String            unit;
  private TitledBorder     title;
  public PSwing            pSwing;
    
  private String getText() {
    return name + " = " + getValue() + " " + unit;
  }
  

  public void setText() {
    title.setTitle(getText());
  }
  

  public double get() {
    return (double) getValue();
  }
  
  public Slider(String name, String unit, 
                int low, int def, int high, int step) {
    
    super(low, high, def);
    this.name = name;
    this.unit = unit;
    
    setMinorTickSpacing(1);
    setMajorTickSpacing(5);
    setPaintTicks(true);
    setSnapToTicks(false);
    setPaintTrack(true);
    setPaintLabels(false);
    // setBorder(BorderFactory.createTitledBorder(label));
    
    Border redline = BorderFactory.createLineBorder(Color.red);
    title = BorderFactory.createTitledBorder(redline, getText());
    // title.setTitlePosition(TitledBorder.ABOVE_TOP);
    setBorder(title);
    
    // import org.jcpsim.gui.mySliderUI;
    // setUI(new mySliderUI());
    
    addChangeListener(new ChangeListener() {
      
      public void stateChanged(ChangeEvent e) {
        Slider source = (Slider) e.getSource();
        if (!source.getValueIsAdjusting()) {
          source.setText();
//        source.prog.update();
        }
      }
    });
    
    pSwing = new PSwing(this);
  }
}
