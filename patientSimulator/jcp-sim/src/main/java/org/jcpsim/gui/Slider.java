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
