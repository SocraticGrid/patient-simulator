package org.jcpsim.pkpd;


public class util {

  public util() { }

  public static double[] Cube(double k10, double k12, double k21,
                              double k13, double k31, double k41) {

    double a0, a1, a2;   // factors in cubic equation
    double p, q;         // factors in transformed equation
    double phi;          // used for root solving
    double r1;           // also used for root solving

    double r[] = new double[4];

    if (k31 > 0) {   // three compartment  (see Hull, p371)
                     // take roots of X*X*X + a2*X*X + a1*X + a0 = 0

      a0   = k10*k21*k31;
      a1   = k10*k31 + k21*k31 + k21*k13 + k10*k21 + k31*k12;
      a2   = k10 + k12 + k13 + k21 + k31;

      // now transform to x^3 + px + q = 0

      p    = a1 - (a2*a2/3.0);
      q    = (2*a2*a2*a2/27.0) - (a1*a2/3.0) + a0;
      r1   = Math.sqrt(-(p*p*p)/27.0);
      phi  = (-q/2.0)/r1;

      if      (phi >  1)  phi =  1;
      else if (phi < -1)  phi = -1;

      phi  = (Math.acos(phi)/3.0);
      r1   = 2.0 * Math.exp(Math.log(r1)/3.0);

      r[0] = -(Math.cos(phi)*r1 - a2/3.0);
      r[1] = -(Math.cos(phi + Math.PI *120.0/180.0) * r1 - a2/3.0);
      r[2] = -(Math.cos(phi + Math.PI *240.0/180.0) * r1 - a2/3.0);

    } else if (k21 > 0) {  // two compartment; take roots of X*X-a1*X+a0=0
      a0   = k10 * k21;
      a1   = -(k10 + k12 + k21);
      r[0] = (-a1 + Math.sqrt(a1*a1 - 4*a0)) / 2;
      r[1] = (-a1 - Math.sqrt(a1*a1 - 4*a0)) / 2;
      r[2] = 0;

    } else {                  // one compartment model
      r[0] = k10;       r[1] = 0;
      r[2] = 0;
    }

    double temp;                          // sort:  r[0] > r[1] > r[2]
    if (r[1] > r[0])  { temp=r[1];  r[1]=r[0];  r[0]=temp; }
    if (r[2] > r[0])  { temp=r[2];  r[2]=r[0];  r[0]=temp; }
    if (r[2] > r[1])  { temp=r[2];  r[2]=r[1];  r[1]=temp; }

    r[3] = k41;

    return r;
  }
}