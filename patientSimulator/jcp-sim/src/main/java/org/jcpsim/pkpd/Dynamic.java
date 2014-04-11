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