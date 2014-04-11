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


import org.jcpsim.gui.InputElement;
import org.jcpsim.ode.DifferentialEquation;
import org.jcpsim.parameter.Input;
import org.jcpsim.parameter.Output;
import org.jcpsim.parameter.DeOutput;
import org.jcpsim.parameter.DEsOutput;
import org.jcpsim.plot.PlotNode;
import org.jcpsim.plot.Trace;
import static org.jcpsim.units.Unit.*;
import org.jcpsim.run.Scenario;

import edu.umd.cs.piccolo.nodes.PText;
import org.jcpsim.run.Global;


public class SimpleRespirator extends Scenario {

  // given values
  Input     PEEP;        // PEEP [mbar]
  Input     f;           // frequency [/min]
  Input     Ti;          // inspiratory time [%]
  Input     Tp;          // inspiratory pause [%]
  Input     TV;          // tidal volume [l]
  Input     R;           // pulmonary resistance [mbar/(l/s)]
  Input     C;           // pulmonary compliance [l/mbar]

  Output    Presp;       // [mbar]
  Output    V;           // [l]
  Output    F;           // [l/s]
  
  // computed values
  double    cycleTime;   // [s]
  double    tInsp;       // [s]
  double    tInspFlow;   // [s]
  double    qsoll;       // [l/s]
   
  // resulting values
  double    timeInCycle; // [s]
  int       Phase;       // [1|2|3]

  // state values
  DEsOutput time;        // [s]
  DeOutput  Plung;       // [mbar]

  Block     vent; 
  Block     lung;
  
  int       tPlot     = 10;  // [s]
  int       fSampling = 50;  // [1/s]
  int       traces    = tPlot * fSampling;  
  
  PlotNode  plot[];
  PText     text; 
    private final Global global;
  
  // ---------------------------------------------------------------------
  
  public int getPhase(double t) {
    timeInCycle = t % cycleTime;
    if (timeInCycle > tInsp    )  return 3; // exsp.
    if (timeInCycle > tInspFlow)  return 2; // insp. pause
                                  return 1; // insp. flow
  }
  
  public void compute(int i) {
      System.out.println("Computing!");
    Presp.set(0);
    V.set(0);
    F.set(0);
  }
  
  public void step(int n) {
    time.stepDelta(1 / (double)fSampling);
    for (PlotNode p:plot)  p.update();
    vent.macroTimeStep(time.get());
    lung.macroTimeStep(time.get());
  }
  
  // -----------------------------------------------------------------------------------
  
  public SimpleRespirator(Global global)  {
    
    super("Simple Respirator");

    this.global = global;
    
    addBlock(vent = new Block("Ventilator", 710, 0, this, global));
    TV    = vent.addI("TidalVolume", V_l,        0.0,  0.7,   8.0, 0.01, InputElement.InputMode.Drag);
    PEEP  = vent.addI("PEEP",        P_mbar,   -10.0,  5.0,  40.0, 1.0,  InputElement.InputMode.Drag);
    f     = vent.addI("Frequency",   f_min,      4.0, 12.0, 120.0, 1.0,  InputElement.InputMode.Drag);
    Ti    = vent.addI("InspTime",    _perc,      0.0, 40.0,  80.0, 1.0,  InputElement.InputMode.Drag);
    Tp    = vent.addI("PauseTime",   _perc,      0.0, 10.0,  80.0, 1.0,  InputElement.InputMode.Drag);
    
    
    addBlock(lung = new Block("Lung", 710, 300, this, global));
    R     = lung.addI("Resistance",  R_mbar_l_s, 2.0, 10.0, 100.0,   1.0, InputElement.InputMode.Drag);
    C     = lung.addI("Compliance",  C_l_mbar,  0.01, 0.04,   0.2, 0.002, InputElement.InputMode.Drag);
    Presp = lung.addO("Presp",       P_mbar,     -10,   80, 0.1);
    V     = lung.addO("Vlung",       V_l,     -0.100,  2.0, 0.01);
    F     = lung.addO("F",           F_l_s,     -2.0,  2.0, 0.01);
    time  = lung.addD("t",           t_s,          0, tPlot, 0.1, 0.0, 1.0);

    Plung = time.add("Plung", P_mbar, -10, 80, 0.1,
      new DifferentialEquation() {
      
        public double initialValue() { return PEEP.get(); }

        public double dxdt(double t) {
          
          cycleTime = 60.0 / f.get();
          tInsp     = 0.01 * cycleTime * Ti.get();
          tInspFlow = 0.01 * cycleTime * (Ti.get()-Tp.get());
          qsoll     = TV.get() / tInspFlow;
          
          switch (getPhase(t)) {
            case 1:  
                System.out.println("Phase 1");
                Presp.set(qsoll * R.get() + get());
                break;
            case 2:  
                System.out.println("Phase 2");
                Presp.set(get());
                break;
            case 3:  
                System.out.println("Phase 3");
                Presp.set(PEEP.get());
                break;
          }
          F.set((Presp.get() - get()) / R.get());
          V.set(get() * C.get());
          
          return F.get() / C.get();
        }
      }
    );
    
    compute(0);
    
    int plotW = 350;  // width of a plot
    int plotH = 250;  // height of a plot
    int plotD = 5;    // distance between plots

    plot   =new PlotNode[6];
    plot[0]=new PlotNode("Pressure",true, new Trace[]{new Trace("Prespirator", time, Presp, traces, 1),                                                          
                                                      new Trace("Plung",       time, Plung,traces, 1)});
    plot[1]=new PlotNode("Volume",  true,             new Trace("Vlung",       time, V, traces, 1));
    plot[2]=new PlotNode("Flow",    true,             new Trace("Flow",        time, F, traces, 1));
    
    Trace trace3a = new Trace("Prespirator",Presp, V, traces, 1);
    Trace trace3b = new Trace("Plung",      Plung, V, traces, 1);
    plot[3]=new PlotNode("PV-loop", false,new Trace[]{trace3a, trace3b});
    
    Trace trace4a = new Trace("Prespirator",Presp, F, traces, 1);
    Trace trace4b = new Trace("Plung",      Plung, F, traces, 1);
    plot[4]=new PlotNode("PF-loop", false,new Trace[]{trace4a,  trace4b});
    
    Trace trace5 = new Trace("V/F", V, F, traces, 1);
    plot[5]=new PlotNode("VF-loop", false, trace5);
    
    for (int i=0; i<plot.length; i++) {
      plot[i].setBounds(0 + (i/3) * (plotW+plotD), 
                        0 + (i%3) * (plotH+plotD), plotW, plotH);
      addChild(plot[i]);
    }
    setBounds(getUnionOfChildrenBounds(null));
  }
}