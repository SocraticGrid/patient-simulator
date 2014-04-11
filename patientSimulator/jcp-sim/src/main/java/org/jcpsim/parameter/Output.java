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
