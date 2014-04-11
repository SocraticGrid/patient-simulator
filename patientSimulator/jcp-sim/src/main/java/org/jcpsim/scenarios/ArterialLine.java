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

import java.awt.Color;

import org.jcpsim.ode.DifferentialEquation;
import org.jcpsim.parameter.Input;
import org.jcpsim.parameter.Output;
import org.jcpsim.parameter.DeOutput;
import org.jcpsim.parameter.DEsOutput;
import org.jcpsim.plot.PlotNode;
import org.jcpsim.plot.Trace;
import static org.jcpsim.units.Unit.*;
import org.jcpsim.util.Bezier;
import org.jcpsim.run.Scenario;
import org.jcpsim.gui.InputElement;


import edu.umd.cs.piccolo.nodes.PText;
import org.jcpsim.run.Global;


public class ArterialLine extends Scenario {

  // given values
  Input     Wave;      // number of wavwform
  Input     Flush;     // flush [yes|no]
  Input     Damp;      // correct - underdamped - overdamped    
  Input     Rline;     // resistance
  Input     Cline;     // compliance 
  Input     Lline;     // inductance
  
  Output    Freq;      // natural frequency Fn
  Output    DampCoeff; // damping coefficient
  
  // state values
  DEsOutput time;      // 
  DeOutput  qLine;     // 
  DeOutput  pMeas;     //
  
  final double RflushTrue  = 0.2;     
  final double RflushFalse = 200 * 3600 / 2;     
  final double pFlush      = 300;     

  // resulting values
  double Rflush;     
  double qFlush;    

  Output _pPatient;
  Output _pMeas;
  
  PlotNode plot;
  PText    text; 
  
  Block block;
  
  int tPlot     =  8;  // [s]
  int fSampling = 50;  // [1/s]
  int traces    = tPlot * fSampling;  
  
  Bezier broadWave;
  Bezier narrowWave;
    private final Global global;
  
  // ---------------------------------------------------------------------
  
  private class broadBezier extends Bezier {
    public broadBezier() {
      super(1, 337);
      add(  8.55, 341.27,  11.90, 330.94,  13.57, 325.00);
      add( 18.53, 307.42,  20.69, 288.08,  23.27, 270.00);
      add( 31.93, 209.24,  40.55, 147.13,  46.00,  86.00);
      add( 47.37,  70.63,  49.39,  52.22,  55.63,  38.00);
      add( 57.70,  33.27,  59.74,  26.13,  65.04,  24.17);
      add( 72.53,  21.40,  83.52,  36.56,  89.00,  40.98);
      add( 94.43,  45.38, 101.00,  45.28, 106.99,  48.45);
      add(121.25,  56.00, 138.99,  69.63, 149.56,  82.00);
      add(179.93, 117.57, 177.93, 168.19, 210.17, 201.96);
      add(228.96, 221.64, 253.71, 214.88, 276.23, 225.49);
      add(293.18, 233.67, 303.90, 249.98, 317.51, 262.57);
      add(325.69, 270.14, 337.24, 273.73, 346.00, 280.67);
      add(360.54, 292.18, 373.44, 305.92, 389.00, 316.50);
      add(394.58, 320.30, 402.03, 321.46, 408.00, 324.87);
      add(414.75, 328.73, 430.56, 342.48, 433.00, 327.00);
    }
    public double get(double x) { return 160-getY(x)*0.25; }
  }
  
  private class narrowBezier extends Bezier {
    public narrowBezier() {
      super(1, 331);
      add( 10.27, 331.72,  11.23, 322.08,  13.15, 315.00);
      add( 18.02, 297.00,  21.56, 278.47,  24.13, 260.00);
      add( 32.57, 199.43,  36.94, 138.38,  46.92,  78.00);
      add( 49.34,  63.35,  51.10,  48.32,  55.16,  34.00);
      add( 56.51,  29.21,  59.36,  21.33,  64.10,  18.89);
      add( 70.42,  15.65,  79.81,  33.57,  82.39,  38.00);
      add( 90.52,  51.94,  92.00,  72.48,  96.43,  88.00);
      add( 99.24,  97.84, 105.10, 105.62, 108.45, 115.00);
      add(116.67, 137.98, 128.85, 158.86, 137.58, 182.00);
      add(150.07, 215.12, 158.38, 257.45, 191.00, 277.53);
      add(201.44, 283.96, 205.87, 275.84, 215.00, 271.27);
      add(222.46, 267.54, 232.88, 266.48, 241.00, 268.79);
      add(254.52, 272.63, 266.49, 283.81, 278.00, 291.49);
      add(301.89, 307.41, 327.36, 321.38, 349.00, 340.00);
    }
    public double get(double x) { return 160-getY(x)*0.25; }
  }
  
  public double pPatient(double t) {
    switch ((int)Wave.get()) {
      case 0:  return broadWave.get(t);
      case 1:  return narrowWave.get(t);
      default: return 100;
    }
  }
  
  
  public void compute(int i) {
    _pPatient.set(pPatient(0));
  }
  
  public void step(int n) {
    time.stepDelta(1 / (double)fSampling);
    _pPatient.set(pPatient(time.get()));
    plot.update();
    block.macroTimeStep(time.get());
  }

  // -----------------------------------------------------------------------------------
  
  public ArterialLine(Global global)  {
    super("Arterial Line");

    this.global = global;
    
    broadWave  = new broadBezier();
    narrowWave = new narrowBezier();
    addBlock(block = new Block("ArterialLine", 710, 0, this, global));
    
    Wave  = block.addI("Wave",        no_unit,      0.0,   0.0,   2.0,  1.0,   InputElement.InputMode.Click);
    Flush = block.addI("Flush",       no_unit,      0.0,   0.0,   1.0,  1.0,   InputElement.InputMode.Press);
    Damp  = block.addI("Damping",     no_unit,      0.0,   0.0,   2.0,  1.0,   InputElement.InputMode.Click);
    Rline = block.addI("Resistance",  R_mmHg_ml_s,  0.06,  0.58,  4.0,  0.02,  InputElement.InputMode.Drag);
    Lline = block.addI("Inertance",   L_ss_mmHg_ml, 0.002, 0.02,  0.04, 0.002, InputElement.InputMode.Drag);
    Cline = block.addI("Compliance",  C_ml_mmHg,    0.01,  0.1,   0.4,  0.01,  InputElement.InputMode.Drag);
    time  = block.addD("t",           t_s,          0,     tPlot, 0.01, 0.0, 1.0);
        
    qLine = time.add("Flow", F_ml_s, -1.0, 1.0, 0.01,
      new DifferentialEquation() {
        public double initialValue() { return 0; }
        public double dxdt(double t) {
          return  (pPatient(t) - Rline.get() * get() - pMeas.get()) / Lline.get();
        }
      }
    );
        
    pMeas = time.add("Presp", P_mmHg, 0, 300, 0.1,
      new DifferentialEquation() {
        public double initialValue() { return pPatient(0); }
        public double dxdt(double t) {
          Rflush = (Flush.get() < 0.5) ? RflushFalse : RflushTrue;  
          qFlush = (pFlush - pMeas.get()) / Rflush;
          return  (qLine.get() + qFlush) / Cline.get();
        }
      }
    );
   
    Freq  = block.addO(new Output(block, "Frequency", f_Hz, 0.0, 50.0, 0.1) {
      public double get() { return 1.0 / (2.0 * Math.PI * Math.sqrt(Lline.get() * Cline.get())); }});
    
    DampCoeff = block.addO(new Output(block, "DampingCoeff", no_unit, 0.0, 10.0, 0.01) {
      public double get() { return Rline.get() / 2.0 * Math.sqrt(Cline.get() / Lline.get()); }});
  
   _pPatient   = block.addO("Preal",   P_mmHg,     0,   300, 0.1);
    
    compute(0);

    Trace trace1 = new Trace("at Patient",    time, _pPatient, traces, 2, 5.0f, new Color(  0,0,255,128), null, Trace.LINES, null);
    Trace trace2 = new Trace("at Transducer", time , pMeas,    traces, 2, 5.0f, new Color(255,0,  0,128), null, Trace.LINES, null);
    
    plot = new PlotNode("Arterial Line Plot",true, new Trace[] { trace1, trace2 });
    
    plot.setBounds(0, 0, 700, 500);
    addChild(plot);
    setBounds(getUnionOfChildrenBounds(null));
  }
}