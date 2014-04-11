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
package org.jcpsim.ode;

/**
 * Superclass for a differential equation.
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id: DifferentialEquation.java,v 1.1 2006/08/22 23:32:51 jcpsim Exp $
 * TODO: should DifferentialEquation extend Result ???
 */

public abstract class DifferentialEquation {

  private double x;
  private double dxdt;

  private double getx;
  /**
   * Gets the value of the state variable. Initial values are set
   * by {@link #setInitialValue()}.
   */
  public  double get() { return getx; }
  public  double _get() { return getx; }

  // --------------------------------------------------------------------------
  /**
   * Has to be overridden.
   */
  public abstract double initialValue();
  /**
   *
   */
  public void setInitialValue() { getx = x = initialValue(); }
  /**
   * Has to be overridden. x can be accessed with get().
   */
  public abstract double dxdt(double t);

  // --------------------------------------------------------------------------
  /**
   * The simplest integration method (should only be used for testing
   * purposes).
   * Performs one integration step of this differential equation with fixed
   * stepsize <code>dt</code>
   */
  public void step_Euler(double t, double dt) {
    dxdt = dxdt(t);
    getx = x = x + dt * dxdt;
  }

  // --------------------------------------------------------------------------
  // fourth-order Runge-Kutta algorithm.

  private double dxm;
  private double dxt;

  void rk4_1(double t, double dt2) {
    dxdt = dxdt(t);
    getx = x + dt2 * dxdt;
  }

  void rk4_2(double t, double dt2) {
    dxt  = dxdt(t);
    getx = x + dt2 * dxt;
  }

  void rk4_3(double t, double dt) {
    dxm  = dxdt(t);
    getx = x + dt  * dxm;  dxm += dxt;
  }

  void rk4_4(double t, double dt6) {
    dxt  = dxdt(t);
    getx = x  = x + dt6 * (dxdt+dxt+dxm+dxm);
  }

  // --------------------------------------------------------------------------
  /**
 * Runge-Kutta-Fehlberg algorithm with Cash-Karp parameters.
 * <p>
 * Theory can be found in: Press WH et al.: Numerical Recipes in C, 2nd ed.,
 * Chapter 16.2: Adaptive Stepsize Control for Runge-Kutta.
 */

  private double ak2;
  private double ak3;
  private double ak4;
  private double ak5;
  private double ak6;
  private double xerr;
  private double xtemp;
  private double xscal;

  // Cash-Karp Parameters for Embedded Runge-Kutta
  //    (Numerical Recipes in C, 2nd ed, p 717)
  private static final double b21 = 0.2;
  private static final double b31 = 3.0 / 40.0;
  private static final double b32 = 9.0 / 40.0;
  private static final double b41 = 0.3;
  private static final double b42 = -0.9;
  private static final double b43 = 1.2;
  private static final double b51 = -11.0 / 54.0;
  private static final double b52 = 2.5;
  private static final double b53 = -70.0 / 27.0;
  private static final double b54 = 35.0 / 27.0;
  private static final double b61 = 1631.0 / 55296.0;
  private static final double b62 = 175.0 / 512.0;
  private static final double b63 = 575.0 / 13824.0;
  private static final double b64 = 44275.0 / 110592.0;
  private static final double b65 = 253.0 / 4096.0;

  private static final double c1  = 37.0 / 378.0;
  private static final double c3  = 250.0 / 621.0;
  private static final double c4  = 125.0 / 594.0;
  private static final double c6  = 512.0 / 1771.0;

  private static final double dc5 = -277.0 / 14336.0;
  private static final double dc1 = c1 - 2825.0 / 27648.0;
  private static final double dc3 = c3 - 18575.0 / 48384.0;
  private static final double dc4 = c4 - 13525.0 / 55296.0;
  private static final double dc6 = c6 - 0.25;

  private static final double TINY = 1.0e-30;


  void rk45_1a(double dt) { getx=xtemp=x+dt*(b21*dxdt); }

  void rk45_1b2a(double t, double dt) {
    ak2  = dxdt(t);
    getx = xtemp = x+dt*(b31*dxdt+b32*ak2);
  }

  void rk45_2b3a(double t, double dt) {
    ak3  = dxdt(t);
    getx = xtemp = x+dt*(b41*dxdt+b42*ak2+b43*ak3);
  }

  void rk45_3b4a(double t, double dt) {
    ak4  = dxdt(t);
    getx = xtemp = x+dt*(b51*dxdt+b52*ak2+b53*ak3+b54*ak4);
  }

  void rk45_4b5a(double t, double dt) {
    ak5  = dxdt(t);
    getx = xtemp = x+dt*(b61*dxdt+b62*ak2+b63*ak3+b64*ak4+b65*ak5);
  }

  void rk45_5b6a(double t, double dt) {
    ak6   = dxdt(t);
    xtemp = x+dt*( c1*dxdt+ c3*ak3+ c4*ak4+         c6*ak6);
    xerr  =   dt*(dc1*dxdt+dc3*ak3+dc4*ak4+dc5*ak5+dc6*ak6);
    getx  = x;
  }

  void rk45_6b(double t)  { dxdt = dxdt(t); }

  double getScaledError() { return  Math.abs(xerr / xscal); }
  void   update()         { getx = x = xtemp; }

  void setScale(double dt) {
    xscal = Math.abs(x) + Math.abs(dxdt * dt) + TINY;
  }
}