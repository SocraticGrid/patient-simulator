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
package org.jcpsim.examples.simple;

import java.util.Locale;


public class VentilationOfRcLung {

  // given values
  double PEEP =   5.0;  // PEEP [mbar]
  double Freq =  12.0;  // frequency [/min]
  double Ti   =  40.0;  // inspiratory time [%]
  double Tp   =  10.0;  // inspiratory pause [%]
  double TV   = 400.0;  // tidal volume [ml]
  double R    =  10.0;  // pulmonary resistance [mbar/(l/s)]
  double C    =  40.0;  // pulmonary compliance [ml/mbar]
  
  // computed values
  double cycleTime = 60.0 / Freq;                  // [s]
  double tInsp     = cycleTime * Ti      / 100.0;  // [s]
  double tInspFlow = cycleTime * (Ti-Tp) / 100.0;  // [s]
  double qsoll     = TV / tInspFlow;               // [ml/s]
  
  // resulting values
  double timeInCycle;  // [s]
  int    Phase;        // [1|2|3]
  double Presp;        // [mbar]
  double q;            // [ml/s]
  double V;            // [ml]

  // state values
  double Plung;  // [mbar]
  
  // ---------------------------------------------------------------------
  
  public int getPhase(double t) {
    timeInCycle = t % cycleTime;
    if (timeInCycle > tInsp    )  return 3; // exsp.
    if (timeInCycle > tInspFlow)  return 2; // insp. pause
                                  return 1; // insp. flow
  }
  

  public VentilationOfRcLung() {
    
    double deltaT = 0.01;
    Plung = PEEP;
    
    for (double t=0.0; t <= 10.0; t+=deltaT) {
      switch (getPhase(t)) {
        case 1:  Presp = qsoll * R / 1000.0 + Plung;  break;
        case 2:  Presp = Plung;                       break;
        case 3:  Presp = PEEP;                        break;
      }
      q = (Presp - Plung) / (R / 1000.0);
      V = Plung * C;
      Plung += deltaT * q / C;
      
      System.out.printf(Locale.US, "%8.4f, %8.4f, %8.4f, %8.4f, %8.4f\n", 
                        t, Presp, Plung, V, q);
    }
    System.exit(0);
  }

  
  public static void main(String[] args) { new VentilationOfRcLung(); }
}
