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