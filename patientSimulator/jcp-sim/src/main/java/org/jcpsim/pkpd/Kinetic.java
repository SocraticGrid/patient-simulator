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


import java.io.PrintStream;

/**
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 */
public class Kinetic {

  // ---------- micro constants [/s]
  double k10;          // elimination (k_el)
  double k12;
  double k13;
  double k14;
  double k21;
  double k31;
  double k41;          // effect hysteresis (k_e0), only one effect

  // ---------- distribution volumes [l]
  double V1;           // volume of central compartment (Vc)
  double V2;
  double V3;
  double V4;           // volume of effect site (Ve = Vc * 0.00001)

  // ---------- clearances [l/s]
  double Cl1;
  double Cl2;
  double Cl3;

  public double perfusor;       // [mg/s] actual perfusor rate
  public double MassInfused;    // [mg]

  int      compartments;   // 1-3
  int      effects;        // up to now: only 0-1

  // --------------------------------------------------------------------------

  // macro parameters:  A, B, C, alpha, beta, gamma
  // micro constants :  k_ij, Vc, Cl

  double lambda[] = new double[4];     // [1/s]   (alpha, beta, gamma, k_e0)
  double p_coef[] = new double[4];     // [mg/l]  (A, B, C)
  double e_coef[] = new double[4];     // [mg/l]

  double tHalf_alpha;                  // [s]
  double tHalf_beta;                   // [s]
  double tHalf_gamma;                  // [s]
  double tHalf_ke0;                    // [s]

  double p_udf[];                      // plasma unit disposition function
  double e_udf[];                      // effect unit disposition function

  double peak_time;                    // [s]  when peak effect is reached

  // --------------------------------------------------------------------------

  void SetMisc() {
    V4          = V1  * 0.00001;
    k14         = k41 * V4 / V1;
    perfusor    = 0;
    MassInfused = 0;

    if (k41 > 0)  effects = 1;
            else  effects = 0;

    if      (k31 > 0)  compartments = 3;
    else if (k21 > 0)  compartments = 2;
    else               compartments = 1;
  }


  void SetClV() {
    Cl1 = V1 * k10;
    Cl2 = V1 * k12;
    Cl3 = V1 * k13;
    if (k21 > 0)  V2 = Cl2 / k21;  else  V2 = 0;
    if (k31 > 0)  V3 = Cl3 / k31;  else  V3 = 0;
  }


  void SetKxx() {
    k10 = Cl1 / V1;
    k12 = Cl2 / V1;
    k13 = Cl3 / V1;
    if (V2 > 0)  k21 = Cl2 / V2;  else  k21 = 0;
    if (V3 > 0)  k31 = Cl3 / V3;  else  k31 = 0;
  }

  // --------------------------------------------------------------------------

  public Kinetic(double k10, double k12, double k13,
                 double k21, double k31, double k41, double V1) {
    this.k10 = k10;
    this.k12 = k12;
    this.k13 = k13;
    this.k21 = k21;
    this.k31 = k31;
    this.k41 = k41;
    this.V1  = V1;
    SetClV();
    SetMisc();
  }

  // --------------------------------------------------------------------------

  public void InitWithClAndV(double V1, double Cl1,
                             double V2, double Cl2,
                             double V3, double Cl3, double k41) {
    this.V1  = V1;
    this.V2  = V2;
    this.V3  = V3;
    this.Cl1 = Cl1;
    this.Cl2 = Cl2;
    this.Cl3 = Cl3;
    this.k41 = k41;
    SetKxx();
    SetMisc();
  }


  public void InitWitkLambdaAndPcoef(double p0, double l0,
                                     double p1, double l1,
                                     double p2, double l2,
                                     double p3, double l3, double V1) {
    p_coef[0] = p0;
    p_coef[1] = p1;
    p_coef[2] = p2;
    p_coef[3] = p3;

    lambda[0] = l0;
    lambda[1] = l1;
    lambda[2] = l2;
    lambda[3] = l3;

    this.V1 = V1;

    CalculateMicro();
    SetClV();
    SetMisc();
    effects = 0;
  }

  // --------------------------------------------------------------------------
/*
  public final double GetPlasmaC() { return y[0] / V1; }   // [mg/l]
  public final double GetEffectC() { return y[3] / V4; }   // [mg(l]
*/
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------

  public int getN(int n) { return 4; };

  public double setInitialValues(double[] x) {
    x[0] = 0;
    x[1] = 1;
    x[2] = 1;
    x[3] = 1;
    return 0.0;
  }

  public void setValues(double t, double[] x) { }

  public double derivatives(double t, double[] x, double[] dxdt) {
    dxdt[1] = k12*x[0] - k21*x[1];
    dxdt[2] = k13*x[0] - k31*x[2];
    dxdt[3] = k14*x[0] - k41*x[3];
    dxdt[0] = perfusor - (k10*x[0] + dxdt[1] + dxdt[2] + dxdt[3]);
    return t;
  }

  public void stepDone(double t, double[] x) { }

  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------

/*
  public void Bolus(double b)    { perfusor = b / GetTimeStep(); }    // [mg]
*/
  public void Perfuse(double b)  { perfusor = b; }                    // [mg/s]

  // --------------------------------------------------------------------------
/*
  public void TargetPlasmaC(double target) {  // target plasma conc. [mg/l]
    Bolus(Math.max(target * V1 - y[0], 0));
  }
*/
  // --------------------------------------------------------------------------
/*
  public void TargetEffectC(double target) {  // target effect site conc. [mg/l]

    int    i;
    double t;

    double injection_fraction;

    double y1[]    = new double[4];
    double ydot1[] = new double[4];
    double y2[]    = new double[4];
    double ydot2[] = new double[4];

    if (target > y[3]) {                     // raise level if necessary

      double injection     = 1 + y[0];
      double percent_error = 1;

      while (percent_error > 0.0001) {       // perform experiment
        for (i=0; i<4; i++) {
          y1[i] = y[i];
          y2[i] = y[i];
        }
        y1[0] += injection;                  // inject 1 unit

        t        =  0;
        ydot1[3] = -1;
        ydot2[3] = -1;

        while (ydot1[3] < 0) {

          ydot1[1] = y1[0] * k12 - y1[1] * k21;  // with injection
          ydot1[2] = y1[0] * k13 - y1[2] * k31;
          ydot1[3] = y1[0] * k14 - y1[3] * k41;
          ydot1[0] = -(y1[0] * k10 + ydot1[1] + ydot1[2] + ydot1[3]);

          ydot2[1] = y2[0] * k12 - y2[1] * k21;  //  without injection
          ydot2[2] = y2[0] * k13 - y2[2] * k31;
          ydot2[3] = y2[0] * k14 - y2[3] * k41;
          ydot2[0] = -(y2[0] * k10 + ydot2[1] + ydot2[2] + ydot2[3]);

          for (i=0; i<4; i++) {
            y1[i] += dt * ydot1[i];
            y2[i] += dt * ydot2[i];
          }

          t++;
        }

        if (Math.abs((target - y2[3]) / target) < 0.0000001) {
          injection = 0;
          break;
        }

        percent_error      = Math.abs((target - y1[3]) / target);
        injection_fraction = (y1[3] - y2[3]) / injection;

        if (injection_fraction == 0) {
          System.err.println("injection_fraction == 0");
          System.exit(1);
        }
        injection = (target - y2[3]) / injection_fraction;
      }
      Bolus(injection);

    } else if (target == y[3]) {
      TargetPlasmaC(target * k41/k14 / V1);
    } else {
      Bolus(0);
    }
  }


  public void Step() {
    Solve();
    MassInfused += perfusor * GetTimeStep();
  }
*/
  // ------------------------------------------------------------------------

  // see Schwilden!

  double p_conc[];       // initial auf Null setzen !!!
/*
  public void CalcPlasmaConc() {
    p_conc = new double[4];
    for (int i=0; i<4; i++) {
      p_conc[i] *= Math.exp(-lambda[i] * GetTimeStep());
      p_conc[i] += perfusor * (1 - Math.exp(-lambda[i] * GetTimeStep())) /
                   lambda[i];
    }
  }
*/
  // --------------------------------------------------------------------------

  double e_conc[];

  public double CalcEffectConc() {
    e_conc = new double[4];
    e_conc[3] = 0;
    for (int i=0; i<3; i++) {
      e_conc[i]  = p_coef[i] * k41 / (k41 - lambda[i]);
      e_conc[3] -= e_conc[i];
    }
    double abssim = 0;
    for (int i=0; i<4; i++)  abssim += p_conc[i] * e_conc[i];
    return abssim;
  }

  public double CalcBloodConc() {
    double bloodConc = 0;
    for (int i=0; i<4; i++)  bloodConc += p_conc[i] * p_coef[i];
    return bloodConc;
  }

  // --------------------------------------------------------------------------

  // --------------------------------------------------------------------------

  public void CalculatePcoef() {

    p_coef[3] = 0;

    if (k31 > 0) {
      p_coef[0] = (k21-lambda[0])*(k31-lambda[0])/
                  (lambda[0]-lambda[1])/(lambda[0]-lambda[2])/V1/lambda[0];
      p_coef[1] = (k21-lambda[1])*(k31-lambda[1])/
                  (lambda[1]-lambda[0])/(lambda[1]-lambda[2])/V1/lambda[1];
      p_coef[2] = (k21-lambda[2])*(k31-lambda[2])/
                  (lambda[2]-lambda[1])/(lambda[2]-lambda[0])/V1/lambda[2];

    } else if (k21 > 0) {
      p_coef[0] = (k21-lambda[0])/(lambda[1]-lambda[0])/V1/lambda[0];
      p_coef[1] = (k21-lambda[1])/(lambda[0]-lambda[1])/V1/lambda[1];
      p_coef[2] = 0;

    } else {
      p_coef[0] = 1/lambda[0]/V1;
      p_coef[1] = 0;
      p_coef[2] = 0;
    }
  }

  // --------------------------------------------------------------------------

  public void CalculateEcoef() {

    if (k31 > 0) {
      e_coef[0] = p_coef[0] / (k41 - lambda[0]) * k41;
      e_coef[1] = p_coef[1] / (k41 - lambda[1]) * k41;
      e_coef[2] = p_coef[2] / (k41 - lambda[2]) * k41;
      e_coef[3] = (k21-k41)*(k31-k41) /
                  (lambda[0]-k41)/(lambda[1]-k41)/(lambda[2]-k41)/V1;

    } else if (k21 > 0) {
      e_coef[0] = p_coef[0] / (k41 - lambda[0]) * k41;
      e_coef[1] = p_coef[1] / (k41 - lambda[1]) * k41;
      e_coef[2] = 0;
      e_coef[3] = (k21-k41) / (lambda[0]-k41) / (lambda[1]-k41) / V1;

    } else {
      e_coef[0] = p_coef[0] / (k41 - lambda[0]) * k41;
      e_coef[1] = 0;
      e_coef[2] = 0;
      e_coef[3] = 1 / (lambda[0]-k41) / V1;
    }
  }

  // --------------------------------------------------------------------------

  public void CalculateXXX() {

    double AUC = 0;
    for (int i=0; i<compartments; i++)  AUC = p_coef[i] / lambda[i];

  }

  // --------------------------------------------------------------------------

  public void CalculateMicro() {    // (Hull, p184)

    switch (compartments) {

      case 1:  k10 = lambda[0];
               break;

      case 2:  k21 = (p_coef[0] * lambda[1] + p_coef[1] * lambda[0]) /
                     (p_coef[0] + p_coef[1]);
               k10 = lambda[0] * lambda[1] / k21;
               k12 = lambda[0] + lambda[1] - (k10 + k21);
               V2  = V1 * k12/k21;
               break;

      case 3:  double b = (lambda[0] * (p_coef[1] + p_coef[2]) +
                           lambda[1] * (p_coef[0] + p_coef[2]) +
                           lambda[2] * (p_coef[0] + p_coef[1])) /
                          (p_coef[0] +  p_coef[1] + p_coef[2]);

               double c = (lambda[0] *  lambda[1] * p_coef[2] +
                           lambda[0] *  lambda[2] * p_coef[1] +
                           lambda[1] *  lambda[2] * p_coef[0]) /
                          (p_coef[0] +  p_coef[1] + p_coef[2]);

               k31 = (b - Math.sqrt(b*b - 4*c)) / 2;
               k21 = (b + Math.sqrt(b*b - 4*c)) / 2;
               k10 = lambda[0]*lambda[1]*lambda[2] / (k21*k31);
               k12 = ((lambda[0]*lambda[1] +
                       lambda[0]*lambda[2] +
                       lambda[1]*lambda[2]) -
                      k21 * (lambda[0] + lambda[1] + lambda[2]) -
                      k10*k31 + k21*k21) / (k31 - k21);
               k13 = (lambda[0] + lambda[1] + lambda[2]) -
                     (k10 + k12 + k21 + k31);
               V2  = V1 * k12/k21;
               V3  = V1 * k13/k31;
               break;
    }
  }

  // --------------------------------------------------------------------------

  public double C(double t) {
    double conc = 0;
    for (int i=0; i<compartments; i++)
      conc += p_coef[i] + Math.exp(-lambda[i]*t);
    return conc;
  }

  // --------------------------------------------------------------------------

  // calculate udf, plasma concentration, for an infusion of 1/second

  public void CalculatePudf() {

    double temp[] = new double[3];

    p_udf = new double[200];

    for (int i=1; i<199; i++) {
      p_udf[i] = 0;
      for (int j=0; j<3; j++) {
        temp[j]   = temp[j]   *    Math.exp(-lambda[j]) +
                    p_coef[j] * (1-Math.exp(-lambda[j]));
        p_udf[i] += temp[j];
      }
    }
  }

  // --------------------------------------------------------------------------

  // calculate udf, effect site, until peak; note peak as peak_time

  public void CalculateEudf() {

    int     i;
    double  prior;
    double  temp[] = new double[4];

    e_udf = new double[2701];

    boolean effect_data   = true;
    int     delta_seconds = 10;

    if (effect_data) {

      e_udf[0] = 0;
      for (i=1; i<=delta_seconds; i++) {
        e_udf[i] = 0;
        for (int j=0; j<4; j++) {
          temp[j]   = temp[j]   *    Math.exp(-lambda[j]) +
                      e_coef[j] * (1-Math.exp(-lambda[j]));
          e_udf[i] += temp[j];
        }
      }

      i     = delta_seconds;
      prior = e_udf[i-1];

      while (prior < e_udf[i]) {
        prior = e_udf[i++];

        e_udf[i] = 0;
        for (int j=0; j<4; j++) {
          temp[j]  *= Math.exp(-lambda[j]);
          e_udf[i] += temp[j];
        }

        if (i > 2698) {
          System.err.println("Internal error: UDF definition " +
                             "exceeds 2700 elements (45 minutes)");
          System.exit(1);
        }
      }
      peak_time = i-1;
    }
  }

  // --------------------------------------------------------------------------

  public void Exponential() {

    lambda = util.Cube(k10, k12, k21, k13, k31, k41);

    tHalf_alpha = Math.log(2) / lambda[0];
    tHalf_beta  = Math.log(2) / lambda[1];
    tHalf_gamma = Math.log(2) / lambda[2];
    tHalf_ke0   = Math.log(2) / lambda[3];

    CalculatePcoef();
    CalculateEcoef();
    CalculatePudf();
    CalculateEudf();
  }

  // --------------------------------------------------------------------------

  public void PrintInfo(PrintStream os) {

    int i;

    for(i=0; i<4; i++) os.println("lambda["+i+"]: "+lambda[i]*60);

    os.println("Bolus Coefficients");
    for(i=0; i<4; i++) os.println("p_coef["+i+"]: "+p_coef[i]*lambda[i]);
    for(i=0; i<4; i++) os.println("e_coef["+i+"]: "+e_coef[i]*lambda[i]);

    os.println("Infusion Coefficients");
    for(i=0; i<4; i++) os.println("p_coef["+i+"]: "+p_coef[i]/60);
    for(i=0; i<4; i++) os.println("e_coef["+i+"]: "+e_coef[i]/60);

    os.println("peak time for effect: " + peak_time);
  }
}


// ----------------------------------------------------------------------------
/*

  int before_or_after(double ke0) {

    double time[3];
    double result[3];

    lambda[4] = ke0;

    CalculateEcoef(k21, k31, ke0, V1);

    time[0] = t_peak - 0.01;
    time[1] = t_peak + 0.00;
    time[2] = t_peak + 0.01;

    for (int i=0; i<3; i++) {
      result[i] = 0;
      for (int j=0; j<4; j++)
        result[i] += lambda[j] * e_coef[j] * Math.exp(-lambda[j] * time[i]);
    }

    if (result[0]<result[1] && result[2]<result[1])  return 0;  // peak found
    if (result[0]>result[1])  return -1;  // peak is earlier, must lower ke0
    else  return 1;  // peak is later, must raise ke0
  }


  double recalculate_ke0() {

    double too_large, ke0, too_small;
    int    result, count;

    too_small = 0;
    ke0       = k41;
    count     = 0;

    // find too_large first estimate

    too_large = k41 * 1.5;
    while (before_or_after(too_large) > -1)  too_large *= 1.5;

    // Now find new value of ke0 using binary search

    result = before_or_after(ke0);

    while (result != 0) {

      if (result == -1)  too_large = ke0;
                   else  too_small = ke0;

      ke0 = (too_large + too_small) / 2;
      result = before_or_after(ke0);
      if (++count > 100)  exit(-1);
    }
    return ke0;
  }


  public void RecalculateK41(double t_peak) {

    t_peak *= 60; // convert peak time to seconds

    if (t_peak > 0) {

      k41 = recalculate_ke0();
      lambda[4] = k41;

      CalculateEcoef(k21, k31, k41, V1);

      os.println("New ke0 calculated to give peak effect at %f minutes" +
                 t_peak / 60);
      os.println("ke0 (lambda[4]) = " + lambda[4] * 60);
      os.println("t 1/2 ke0 = " +  Math.log(2) / k41 / 60 + " min");
    }
  }
*/
