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

import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.util.logging.Logger;


public class Button extends JButton {
  
  static final long serialVersionUID = 0L;
  
  String            label;
  
  
  public Button(String label, int dx, int dy, PSwingCanvas canvas) {
    super(label);
    this.label = label;
    
    addChangeListener(new ChangeListener() {
      
      public void stateChanged(ChangeEvent e) {
        Button source = (Button) e.getSource();
        System.out.println(source.label + " Event");
      }
    });
    
    PSwing pSwing = new PSwing(this);
    pSwing.translate(dx, dy);
    canvas.getLayer().addChild(pSwing);
  }
}

