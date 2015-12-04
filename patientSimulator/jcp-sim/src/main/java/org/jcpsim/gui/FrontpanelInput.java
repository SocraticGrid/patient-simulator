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

/*
 * jCpSim - Java CardioPulmonary SIMulations (http://www.jcpsim.org)
 *
 * Copyright (C) 2002-2005 Dr. Frank Fischer <frank@jcpsim.org>
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

import org.jcpsim.parameter.Input;
import org.jcpsim.run.Global;

import java.util.logging.Logger;

/**
 * FrontpanelElement associated with a GivenParameter
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 */
public class FrontpanelInput extends FrontpanelElement {

  static final long serialVersionUID = 0L;
  private static final Logger logger = Logger.getLogger(FrontpanelInput.class.getName());

  private Input             input;
//  private ChangeEvent       changeEvent = null;
//  private EventListenerList listenerList = new EventListenerList();

  public Input getInput() { return input; }
  
  private InputMode mode;
  
  // --------------------------------------------------------------------------
  
  public enum InputMode {
    Press,  // left mose key down = 1; up = 0
    Click,  // cyclic increment with each left mouse key click or mouse wheel
    Drag;   // 
  }
  
  // --------------------------------------------------------------------------
/*
  public FrontpanelInput(Input input) {
    this(0,0,0,0,input, InputMode.Drag);
  }
*/
  public FrontpanelInput(double x, double y, double w, double h, Input input, InputMode mode, Global global) {
    super(x, y, w, h, global);
    if (input == null)  logger.severe("input == null");
    this.input = input;
    this.mode  = mode;
  }

  // --------------------------------------------------------------------------

  public String getName() {
    return  input.getName();
  }

  // --------------------------------------------------------------------------
  /**
   * Displays help for that element in the help browser.
   */
  public void help()    { getGlobal().help(input.toUrl()); }
  public void entered() { getGlobal().status(input.toString()); }
  public void exited()  { getGlobal().status(""); }
  
  
  public void start()     { input.set(input.getMin()); getGlobal().status(input.toString()); repaint(); }
  public void toDefault() { input.setToDefault();      getGlobal().status(input.toString()); repaint(); }
  public void end()       { input.set(input.getMax()); getGlobal().status(input.toString()); repaint(); }  
  
  int valueWhenPressed;
  int dragDistance;
  
  public void pressed() {
    valueWhenPressed = input.i;
    dragDistance     = 0;
    switch (mode) {
      case Press:  input.set(1);        getGlobal().status(input.toString());  break;
      case Click:  input.addCyclic(1);  getGlobal().status(input.toString());  break;
    }
  }

  public void released() {
    switch (mode) {
      case Press:  input.set(0);  getGlobal().status(input.toString());  break;
    }
  }
  
  public void step(int x) {
    switch (mode) {
      case Click:  input.addCyclic(1);  break;
      default:     input.add(x);        break;
    }
    getGlobal().status(input.toString());
    repaint();
  }  
  

  
  
  public void drag(int x) {
    dragDistance += x;
    input.setInteger((int)(valueWhenPressed + input.maxint * dragDistance / getScreenBounds(this).getWidth()));
    getGlobal().status(getInput().toString());
    repaint();
//  getInput().valueChanged();
  }
  
  private void switchFocus(int direction) {
    moveMouse(input.getParameterMeta().getFrontpanelInput(this, direction));
  }
  
  public void lastFocus() { switchFocus(-1); }
  public void nextFocus() { switchFocus(+1); }
  
  // --------------------------------------------------------------------------
  
}