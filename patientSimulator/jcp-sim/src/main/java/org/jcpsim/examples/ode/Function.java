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