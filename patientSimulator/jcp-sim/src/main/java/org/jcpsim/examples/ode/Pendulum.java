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


/**
 * Tests ODE solver
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 */
public class Pendulum {


  public class PendulumModel extends DifferentialEquations {

   public PendulumModel() { super(0.0, 0.01, Method.RK45); }

    // - - - constants  - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    double g = 9.8;
    double L = 1;

    // - - - differential equations - - - - - - - - - - - - - - - - - - - - - -
    DifferentialEquation theta = add(new DifferentialEquation() {
      public double initialValue() { return 0.0; }
      public double dxdt(double t) { return v.get(); }
    });
    DifferentialEquation v = add(new DifferentialEquation() {
      public double initialValue() { return 1.0; }
      public double dxdt(double t) { return - g / L * Math.sin(theta.get()); }
    });
  }

  // --------------------------------------------------------------------------

  public Pendulum() {

    PendulumModel m = new PendulumModel();
//  m.setInitialValues();
//  m.setT(0.0);

    while (m.getT() < 1.0) {

      System.out.println(m.dtused  + "   " +
                         m.getT()  + " --> " +
                         m.v.get() + "   " +
                         m.theta.get());
//    m.step_Euler();
//    m.step_RungeKutta4();
      m.step_RungeKutta45();
    }
    System.exit(0);
  }
  // --------------------------------------------------------------------------

  public static void main(String[] args) { new Pendulum(); }
}