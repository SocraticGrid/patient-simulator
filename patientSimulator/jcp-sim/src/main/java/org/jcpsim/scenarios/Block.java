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
package org.jcpsim.scenarios;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.jcpsim.gui.FrontpanelInput;
import org.jcpsim.gui.FrontpanelElement;
import org.jcpsim.gui.InputElement;
import org.jcpsim.gui.OutputElement;
import org.jcpsim.ode.DifferentialEquations;
import org.jcpsim.parameter.Input;
import org.jcpsim.parameter.Output;
import org.jcpsim.parameter.DEsOutput;
import org.jcpsim.parameter.ParameterMeta;
import org.jcpsim.run.Global;
import org.jcpsim.units.Unit;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import java.util.logging.Logger;


public class Block extends PNode implements ParameterMeta {
  private static final Logger logger = Logger.getLogger(Block.class.getName());
  private static final int sliderHeight = 18;
  
  ArrayList<Input>  inputs  = null;
  ArrayList<Output> outputs = null;

  private static Font  titleFont  = new Font("Lucida Sans Regular", Font.BOLD,  14);
  
  int x;
  int y;
  String name;
  PText text;
  
  private Global global;
  
  public Block(String name, int x, int y, PNode node, Global global) {
    super();
    this.x = x;
    this.y = y;
    this.name = name;
    this.global = global;
    addAttribute("name", name);
    setBounds(x, y, 300, 22);
    
    inputs  = new ArrayList<Input>();
    outputs = new ArrayList<Output>();
    
    text = new PText();
    text.setWrapMode(PText.NO_WRAP);
    text.setFont(titleFont);
    text.setJustification(0.5f);
    text.setBounds(x, y+2, 300, 20);
    addChild(text);
    node.addChild(this);
    refresh();
  }
  
  public void macroTimeStep(double time) {
    for (int i=0; i<getChildrenCount(); i++) {
      if (getChild(i) instanceof FrontpanelElement) {
        ((FrontpanelElement)getChild(i)).macroTimeStep(time);
      }
    }
  }
  
  public void refresh() {
    text.setText(getName());
  }
  
  private int getParamHeight() {
    return (sliderHeight+8) * (inputs.size() + outputs.size());
  }
  
  public Input addI(String name, Unit unit, 
                    double min, double def, double max, double step, 
                    InputElement.InputMode mode) {
    Input input = new Input(this, name, unit, min, def, max, step); 
    InputElement   element = new InputElement(x+10, getParamHeight()+y+22, 280, sliderHeight, input, mode, global);
    inputs.add(input);
    addChild(element);
    setBounds(x, y, 300, 22 + getParamHeight());
    
    return input;
  }

  public Output addO(String name, Unit unit, double min, double max, double step) {
    return addO(new Output(this, name, unit, min, max, step));
  }

  public Output addO(Output output) {
    OutputElement element = new OutputElement(x+10, getParamHeight()+y+22, 280, sliderHeight, output, global);
    outputs.add(output);
    addChild(element);
    setBounds(x, y, 300, 22 + getParamHeight());
    return output;
  }
  
  public DEsOutput addD(String key, Unit unit, 
                        double min, double max, double step, 
                        double t, double dt) {
    DEsOutput deso = new DEsOutput(this, t, dt, DifferentialEquations.Method.RK45,
                                   key, unit, min, max, step);
    addO(deso);
    return deso;
  }
  

  public int findFrontpanelInput(FrontpanelInput fpe) {
    for (int i=0; i<getChildrenCount(); i++) {
      if (getChild(i) instanceof FrontpanelInput) {
        if (fpe.equals(getChild(i)))  return i;
      }
    }
    logger.info("FrontpanelInput not found");
    return 0;
  }
  
  public FrontpanelInput getFrontpanelInput(FrontpanelInput fpi, int direction) {
    int pos = findFrontpanelInput(fpi);
    for (int i=0; i<getChildrenCount(); i++) {
      pos = (pos+direction+getChildrenCount()) % getChildrenCount();
      if (getChild(pos) instanceof FrontpanelInput) {
        return  (FrontpanelInput)getChild(pos);
      }
    }
    return fpi;
  }
  
  public void setToDefault() {
    for (Input p:inputs) p.setToDefault();
  }
  
  public String getName() { return Global.i18n(name); }
  public int    getId() { return 0; }
  public String getBlockInfoName() { return name; }
  
  
  public void paint(PPaintContext paintContext) {
    Graphics2D  g2    = paintContext.getGraphics();
    global.setRenderingQuality(g2);
    g2.setPaint(global.getMode().getBlockColor());
    g2.fill(getBounds());
  }      
  
}