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
