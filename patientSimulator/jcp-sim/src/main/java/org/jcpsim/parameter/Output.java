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
package org.jcpsim.parameter;

import org.jcpsim.units.Unit;


/**
 * A Parameter that gets computed during the simulation.
<!--
 *     <ul>
 *     <li>STATE: state variable used to store the actual state of the
 *                simulation (volume in tubing system)</li>
 *     <li>CALC : calculated value (flow through tubing system)</li>
 *     </ul>
-->
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 */
 public class Output extends Parameter {
  
  public enum Type {
    
    /** New value for every time step (volume in lung, default). */
    CONTINUOUS,

    /** New values only after special events.<br>
     * Often derived from CONTINUOUS results
     * (systolic invasive blood pressure). */
    REPETITIVE,

    /** Overall quality / fitness (myocardial oxygen delivery). */
    OVERALL
  }
  
  // --------------------------------------------------------------------------
  
  /**
   * @param meta  Provides information (name etc.) about the parent of the parameter.
   * @param key   The key in the properties file (for I18N).
   * @param unit  The physical unit (like &quot;ml/min&quot;)
   * @param min   The lowest expected value of the parameter.
   *              Used for displaying curves. The parameter can actually be
   *              lower but then it would be out of the plot area.
   * @param max   The highest expected value of the parameter.
   *              Used for displaying curves. The parameter can actually be
   *              higher but then it would be out of the plot area.
   */
  public Output(ParameterMeta meta, String key, Unit unit, double min, double max, double step) {
    super(meta, key, unit, min, max, step);
    setTarget(0.0);
  }
  
  // --------------------------------------------------------------------------
  
  private double  target;     // 

  // ----- optimization -------------------------------------------------------
  /**
   * @param t  Optimal value for this parameter. Default is 0.
   * <ul>
   *   <li>java.lang.Double.POSITIVE_INFINITY: parameter should be maximized</li>
   *   <li>some number: this is the optimal value of the parameter
   *   <li>java.lang.Double.NEGATIVE_INFINITY: parameter should be minimized</li>
   * </ul>
   */
  public void setTarget(double t) { 
    target = t; 
  }
  
  /**
   * @return  Returns optimal value for this parameter.
   */
  public double getTarget() { 
    return target; 
  }
  
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------

  private double  value;
  private double  oldValue;
  
  /**
   * Can be overridden to get an external variable!
   * Should also be overridden for computation of a value that is
   * not needed for the basic simulation (but maybe important for plotting).
   * @return  The actual value of the value ;-)
   */
  public double get() { return value; }
  
  public int getInt() { return  (int)Math.round(value); }
  
  // --------------------------------------------------------------------------
  
  public void set(double value) {
    this.value = value;
//  valueChanged();
  }
  
  public boolean changed() {
    if (oldValue == value)  return false;
    oldValue = value;
    return true;
  }
}
