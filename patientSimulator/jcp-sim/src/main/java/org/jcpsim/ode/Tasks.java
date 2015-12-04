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

/**
 * Tasks which have to be performed before and after the
 * differential equations
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id: Tasks.java,v 1.1 2006/08/22 23:32:51 jcpsim Exp $
 * TODO:    is before and after ever used ???
 * TODO:    it is probably simpler and faster if timeStep and macroTimeStep
 *          (new names) can be registered seperately
 */
public class Tasks {

  /**
   *
   */
  public void before(double t) { }
  /**
   *
   */
  public void after(double t) { }
  /**
   * The step just taken was small enough to achieve sufficient
   * accurate integration of the differential equations.
   * @param t      The time that was reached by the time step (not neccessarily
   *               'real time' !!!).
   * @param dtused The time step that previously was used
   */
  public void afterTimeStep(double t, double dtused) { }
  /**
   * The step just taken was small enough to achieve smooth
   * animations (usually smaller than 0.1 second).
   * @param t  The time that was reached by the time step (not neccessarily
   *           'real time' !!!).
   */
  public void afterMacroTimeStep(double t) { }
}