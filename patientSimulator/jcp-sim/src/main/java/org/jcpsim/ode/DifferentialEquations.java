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
package org.jcpsim.ode;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * Superclass for all differential equations
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id: DifferentialEquations.java,v 1.1 2006/08/22 23:32:51 jcpsim Exp $
 */
public class DifferentialEquations {

  public enum Method {
    EULER,
    RK4,
    RK45
  }

  private Method method;
  
  /** Sets the integration method. */
  public void setMethod(Method m) { method=m; }

  // --------------------------------------------------------------------------
  private double t;
  
  /** Gets the actual simulation time. */
  public double getT()         { return t;     }
  public void setT(double t)   { this.t = t;   }

  // --------------------------------------------------------------------------
  double dt;
  
  /** Sets proposed value for next time step. */
  public void setDt(double x) {
    dt = x;
    dtused = dt;
    setDtmax(dt);
    setDttiny(dt * 0.000100);
    setDtmin( dt * 0.000001);
    dt2 = dt / 2.0;   // for RungeKutta4
    dt6 = dt / 6.0;   // for RungeKutta4
  }
  /** Gets proposed value for next time step. */
  public double getDt() { return dt; }

  // --------------------------------------------------------------------------
  public double dtused;
  /** Gets value used for last time step. */
  public double getDtused() { return dtused; }

  // --------------------------------------------------------------------------
  double dtmax;
  /** Sets biggest allowed time step (default: initial dt). */
  public void setDtmax(double x) { dtmax = x; }
  /** Gets biggest allowed time step (default: initial dt). */
  public double getDtmax() { return dtmax; }

  // --------------------------------------------------------------------------
  double   dtmin;
  /** Sets lowest allowed time step (default: initial dt * 0.000001). */
  public void setDtmin(double x) { dtmin = x; }
  /** Gets lowest allowed time step (default: initial dt * 0.000001). */
  public double getDtmin() { return dtmin; }

  // --------------------------------------------------------------------------
  double dttiny;
  /** Sets time distance before curve break (default: initial dt * 0.0001).
   *  Variable stepsize algorithms will try to step just a tiny distance
   *  before the next break in the curves (as set by
   * {@link DifferentialEquation#dxdt(double)})
   */
  public void setDttiny(double x) { dttiny = x; }
  /** Gets time distance before curve break (default: initial dt * 0.0001).
   *  Variable stepsize algorithms will try to step just a tiny distance
   *  before the next break in the curves (as set by
   * {@link DifferentialEquation#dxdt(double)})
   */
  public double getDttiny() { return dttiny; }

  // --------------------------------------------------------------------------
  double eps;
  /** Sets error tolerance level (default: 0.000001). */
  public void setEps(double x) { eps = x; }
  /** Gets  error tolerance level (default: 0.000001). */
  public double getEps() { return eps;         }

  // --------------------------------------------------------------------------

  private Set<DifferentialEquation> set;   // gets converted to de[] after each add / remove !!!
  public  int n;     // number of DifferentialEquations
  public  DifferentialEquation de[];

  private void setToArray() {
    n  = set.size();
    de = set.toArray(new DifferentialEquation[n]);
  }

  // --------------------------------------------------------------------------

  public void before() { for (int i=0; i<taskN; i++)  st[i].before(t); }
  public void after()  { for (int i=0; i<taskN; i++)  st[i].after(t);  }

  // --------------------------------------------------------------------------
  /**
   * @param t       start time
   * @param dt      initial (and maximum) stepsize (delta t)
   * @param method  integration method
   */
  public DifferentialEquations(double t, double dt, Method method) {
    setT(t);
    setDt(dt);
    setMethod(method);
    setEps(0.000001);
    set = new HashSet<DifferentialEquation>();
    setToArray();
    taskSet = new HashSet<Tasks>();
    taskSetToArray();
  }

  // --------------------------------------------------------------------------

  public DifferentialEquation add(DifferentialEquation eq) {
    if (eq != null) {
      set.add(eq);
      setToArray();
      eq.setInitialValue();
    }
    return eq;
  }
/*
  public DifferentialEquation[] add(DifferentialEquation eq[]) {
    set.addAll(Arrays.asList(eq));
    setToArray();
    for (DifferentialEquation e:eq)  e.setInitialValue();
    return eq;
  }
*/
  public List<DifferentialEquation> add(List<DifferentialEquation> list) {
    set.addAll(list);
    setToArray();
    for (DifferentialEquation e:list)  e.setInitialValue();
    return list;
  }

  public boolean remove(DifferentialEquation eq) {
    boolean flag = set.remove(eq);
    setToArray();
    return flag;
  }

  // --------------------------------------------------------------------------

  private Set<Tasks> taskSet;  // gets converted to st[] after each add / remove !!!
  public  int taskN;    // number of SurroundingTasks
  public  Tasks st[];

  private void taskSetToArray() {
    taskN  = taskSet.size();
    st = taskSet.toArray(new Tasks[taskN]);
  }

  public Tasks addTasks(Tasks st) {
    if (st != null) {
      taskSet.add(st);
      taskSetToArray();
    }
    return st;
  }

  public boolean removeTasks(Tasks st) {
    boolean flag = taskSet.remove(st);
    taskSetToArray();
    return flag;
  }

  // --------------------------------------------------------------------------
 /**
   * Sets all state variables to their initial value at start time.
   */
  public void setInitialValues() {
    for (int i=0; i<n; i++)  de[i].setInitialValue();
  }
  /**
   * Is called just before (#link dxdt).
   * Useful is more than one Numerical model has to be solved.
   * May be overridden.
   * @param t     the actual time
   */
  
  public void setValues(double t) { }
  // --------------------------------------------------------------------------
  
  private double nextStepsize;
  
  /**
   * Can be overridden.
   * @return      if this is greater than <code>0</code>: time to next break
                  <p>Integration algorithms with variable stepsize
                  (like {@link #step_RungeKutta45}) will set their stepsize
                  so that no step will jump over the break in the curves.
   */
  public double getNextStepsize() {
    return nextStepsize;
  }
  
  public void setNextStepsize(double dt) {
    nextStepsize = dt;
  }

  // --------------------------------------------------------------------------

  public void step() {

    switch (method) {
      case EULER:  step_Euler();         break;
      case RK4  :  step_RungeKutta4();   break;
      case RK45 :
      default   :  step_RungeKutta45();  break;
    }
    for (int i=0; i<taskN; i++)  st[i].afterTimeStep(t, dtused);
  }

  // --------------------------------------------------------------------------

  /**
   * Should be called often enough to achieve smooth animations (ca. 10 Hz).
   * TODO: Step exactly to 'until' time *and* consider 'nextStepSize' !
   * TODO: stepIntervall(double t)
   */
  public void stepUntil(double until) {

    while (t < until) {
      setNextStepsize(until-t);
      step();
    }
//  System.out.println("DifferentialEquations.stepUntil: " + t);
    for (int i=0; i<taskN; i++)  st[i].afterMacroTimeStep(t);
  }

  public void stepDelta(double delta) {
    stepUntil(t + delta);
  }
  
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  /**
   * The simplest integration method (should only be used for testing
   * purposes).
   * Performs one integration step of all differential equations with fixed
   * stepsize <code>dt</code>
   */
  public void step_Euler() {

    dtused = nextStepsize;
    if ((dtused <= 0.0) || (dtused > dt))  dtused = dt;

    before();
    for (int i=0; i<n; i++)  de[i].step_Euler(t, dtused);
    t += dtused;
    after();
  }

  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------

  private double   dt2;
  private double   dt6;

  /**
   * Implements the fourth-order Runge-Kutta algorithm.
   */
  public void step_RungeKutta4() {

    before();
    for (int i=0; i<n; i++)  de[i].rk4_1(t,     dt2);
    after();

    before();
    for (int i=0; i<n; i++)  de[i].rk4_2(t+dt2, dt2);
    after();

    before();
    for (int i=0; i<n; i++)  de[i].rk4_3(t+dt2, dt );
    after();

    before();
    for (int i=0; i<n; i++)  de[i].rk4_4(t+dt,  dt6);
    after();

    dtused = dt;
    t += dt;
  }

  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  /**
 * Runge-Kutta-Fehlberg algorithm with Cash-Karp parameters.
 * <p>
 * Theory can be found in: Press WH et al.: Numerical Recipes in C, 2nd ed.,
 * Chapter 16.2: Adaptive Stepsize Control for Runge-Kutta.
 */
  private static final double a2  = 0.2;
  private static final double a3  = 0.3;
  private static final double a4  = 0.6;
  private static final double a5  = 1.0;
  private static final double a6  = 0.875;

  static final double SAFETY =  0.90;
  static final double PGROW  = -0.20;
  static final double PSHRNK = -0.25;
  static final double ERRCON = Math.pow((5.0/SAFETY), (1.0/PGROW));

  // --------------------------------------------------------------------------
  /**
    * performs one integration step with variable stepsize<br>
    * computes:<br>
    * dtused = value just used for last step<br>
    * dt     = proposed value for next step<br>
    */
  public void step_RungeKutta45() {

    double dtlimit = nextStepsize;
    if (dtlimit > 0.0) {
      if (dtlimit>2.0*dttiny)  dtlimit -= dttiny; // step to a tiny distance
                         else  dtlimit  = dttiny; //   before the break
      if (dt > dtlimit)  dt = dtlimit;
    }
    if (dt > dtmax)  dt = dtmax;
    for (int i=0; i<n; i++)  de[i].setScale(dt);

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    double  errmax;
    double  tnew;
    boolean notReady = true;

    before();
    for (int i=0; i<n; i++)  de[i].rk45_6b(t);


    do {
      // ----- start of rkck() -----
      // output: xerr, xtemp;   used but not modified: t, dt
      for (int i=0; i<n; i++)  de[i].rk45_1a(     dt);
      after();

      before();
      for (int i=0; i<n; i++)  de[i].rk45_1b2a(t+a2*dt, dt);
      after();

      before();
      for (int i=0; i<n; i++)  de[i].rk45_2b3a(t+a3*dt, dt);
      after();

      before();
      for (int i=0; i<n; i++)  de[i].rk45_3b4a(t+a4*dt, dt);
      after();

      before();
      for (int i=0; i<n; i++)  de[i].rk45_4b5a(t+a5*dt, dt);
      after();

      before();
      for (int i=0; i<n; i++)  de[i].rk45_5b6a(t+a6*dt, dt);
      after();

      // ----- end of rkck() -----

      errmax = 0.0;
      for (int i=0; i<n; i++)  errmax=Math.max(errmax, de[i].getScaledError());
      errmax /= getEps();

      if ((errmax > 1.0) &&     // truncation error too large, reduce stepsize
          (dt > dtmin)) {       // stepsize too small

        dt *= Math.max(SAFETY * Math.pow(errmax, PSHRNK), 0.1);

        tnew = t+dt;
        if (tnew == t) {
           System.err.println("Stepsize underflow");
           System.exit(1);
        }
      } else  notReady = false;
    } while (notReady);

    for (int i=0; i<n; i++)  de[i].update();

    dtused = dt;        // step succeeded, compute size of next step
    t += dt;

    if (errmax > ERRCON)  dt = SAFETY * dt * Math.pow(errmax, PGROW);
                    else  dt = 5.0 * dt;
  }
}