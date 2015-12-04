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
package org.jcpsim.pkpd;


public class Dynamic {

  public double  Emin;
  public double  Emax;
  public double  c0;
  public double  gamma;

  public boolean isAscending;
  public boolean isSigmoid;
  public double  maxSteepness;

  // --------------------------------------------------------------------------

  public Dynamic(double Emin, double Emax, double c0, double gamma) {
    this.Emin    = Emin;
    this.Emax    = Emax;
    this.c0      = c0;
    this.gamma   = gamma;

    isAscending  = (Emax > Emin);
    isSigmoid    = (gamma > 1);
    maxSteepness = gamma * (Emax-Emin) / (c0 * 4);
  }

  // --------------------------------------------------------------------------

  public double c2E(double c) {
    return Emin + (Emax-Emin) * Math.pow(c, gamma) /
           (Math.pow(c0, gamma) + Math.pow(c, gamma));
  }

  // --------------------------------------------------------------------------

  public double Clip(double E) {
    double Ex = E;
    if (isAscending) {
      if (Ex >= Emax)  Ex = Emax * 0.999;
      if (Ex <  Emin)  Ex = Emin;
    } else {
      if (Ex <= Emax)  Ex = Emax * 1.001;
      if (Ex >  Emin)  Ex = Emin;
    }
    return Ex;
  }

  // --------------------------------------------------------------------------

  public double E2c(double E) {
    return Math.pow(Math.pow(c0, gamma) * (E-Emin) / (Emax-E), 1/gamma);
  }
}