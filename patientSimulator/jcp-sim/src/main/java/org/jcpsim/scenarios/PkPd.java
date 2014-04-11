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

import static org.jcpsim.units.Unit.*;

import java.awt.Color;

import static org.jcpsim.gui.InputElement.*;

import org.jcpsim.parameter.DEsOutput;
import org.jcpsim.parameter.Input;
import org.jcpsim.parameter.Output;
import org.jcpsim.run.Scenario;
import org.jcpsim.plot.Trace;
import org.jcpsim.plot.PlotNode;
import org.jcpsim.run.Global;


public class PkPd extends Scenario {

  Block body;
  Block pump;
  
  Input mass;
  Input give;
  Input rate;
  Input setr;
  
  Output b;
  Output r;
  
  // state values
  DEsOutput time;      //
  
  PlotNode plot;
  
  int tPlot     =  8;  // [s]
  int fSampling = 50;  // [1/s]
  int traces    = tPlot * fSampling;  
    private final Global global;

  
  // -----------------------------------------------------------------------------------
  
  public void step(int n) {
  }

  // -----------------------------------------------------------------------------------
  
  public PkPd(Global global)  {
    super("PkPd");

    this.global = global;
    
    addBlock(body = new Block("Body",         500,   0, this, global));
    addBlock(pump = new Block("InfusionPump", 500, 200, this, global));
    time  = body.addD("t",           t_s,          0,     tPlot, 0.01, 0.0, 1.0);
    
    mass = pump.addI("Bolus",      m_mg,    0.0, 0.0, 200.0, 1.0, InputMode.Drag);
    give = pump.addI("Give bolus", no_unit, 0.0, 0.0,   1.0, 1.0, InputMode.Press);
    rate = pump.addI("Rate",       r_mg_h,  0.0, 0.0, 200.0, 1.0, InputMode.Drag);
    setr = pump.addI("Set rate",   no_unit, 0.0, 0.0,   1.0, 1.0, InputMode.Press);

    b    = pump.addO("Bolus",      m_mg,    0.0, 200.0, 1.0);
    r    = pump.addO("rate",       r_mg_h,  0.0, 200.0, 1.0);

    Trace trace1 = new Trace("Bolus", time,  b, traces, 2, 5.0f, new Color(  0,0,255,128), null, Trace.LINES, null);
    Trace trace2 = new Trace("Rate",  time , r, traces, 2, 5.0f, new Color(255,0,  0,128), null, Trace.LINES, null);
    
    plot = new PlotNode("Boluses and Rate",true, new Trace[] { trace1, trace2 });
    
    plot.setBounds(0, 0, 700, 500);
    addChild(plot);
    
    setBounds(getUnionOfChildrenBounds(null));
  }
}
