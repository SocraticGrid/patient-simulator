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
package org.jcpsim.examples.ode;

import org.jcpsim.ode.*;

import java.util.logging.Logger;

/**
 * Tests ODE solver.
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 */
public class Function {

  int    EQUATIONS =   100;
  int    LOOPS     =  1000;
  int    RUNS      =     4;
  double DT        =   0.1;
  DifferentialEquations.Method METHOD = DifferentialEquations.Method.RK45;

  // --------------------------------------------------------------------------

  public class TestModel extends DifferentialEquations {

    public TestModel() {
      super(0.0, DT, METHOD);
      for (int i=0; i<EQUATIONS; i++) {
        add(new DifferentialEquation() {
          public double initialValue() { return 0.0; }
          public double dxdt(double t) { return t*Math.sqrt(1 + get()*get()); }
        });
      }
    }

    public double nextStepsize(double t) { return 1.0 - t; }
  }

  // --------------------------------------------------------------------------
  /**
   * TODO:  Should this be a real JUnit test ?
   */
  public Function() {
    TestModel m = new TestModel();
    for (int r=0; r<RUNS; r++) {
      long time = System.currentTimeMillis();
      for (int i=0; i<LOOPS; i++) {
        m.setInitialValues();
        m.setT(0.0);
        while (m.getT() < 1.0)  m.step();
      }
      time = System.currentTimeMillis() - time;
      System.out.println("Speed     : " + time);
    }
    System.out.println("Time      : " + m.getT());
    System.out.println("calculated: " + m.de[0].get());
    System.out.println("exact     : " + (Math.exp(0.5) - Math.exp(-0.5)) / 2);
    System.exit(0);
  }
  // --------------------------------------------------------------------------

  public static void main(String[] args) { new Function(); }
}