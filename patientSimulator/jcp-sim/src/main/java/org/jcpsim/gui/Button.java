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

