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
import org.jcpsim.util.util;
import org.jcpsim.run.Global;

/**
 * Stores a value. The value can be accessed with the get() and set() Method.<br>
 *
 * TODO: jCpSim should have global values for SimulationTime, RealTime and
 *       LastStepSize. 
 * TODO: flag for functions with f'(t) &gt;= 0 or f'(t) &lt;= 0; (they are
 *       continuously ascending or descending) Examples: Simulation Time, 
 *       Infused Volume, ... 
 *       These are the functions that cannot not be represented as loops! 
 */
public abstract class Parameter {
  
  
  // --------------------------------------------------------------------------
  
  enum Scale {
    LINEAR, EXPONENTIAL
  }
  
  public Scale scale;
  
  // --------------------------------------------------------------------------
  
  private ParameterMeta meta;
  private String  key;
  private Unit    unit;
  private double  min;     // minimum value
  private double  max;     // maximum value
  private double  step;    // step size or factor (exponential mode), also used for granularity of string representation
  private double  value;   // maximum value
  private boolean zeroInside;   //  is  min < 0 < max ?
  
  public  double  getMin()  { return min; }
  public  double  getMax()  { return max; }
  public double   getStep() { return step;    }
  public  String  getKey()  { return key; }
  /**
   * Returns the id number of the Block the Parameter belongs to. -1: not set.
   */
  public int getId() {
    return meta.getId();
  }
  public String getTypeWithUnit() { return  unit.toString(); }  // Pressure [mmHg]

  
  public double trim(double x) {
    return Math.max(min, Math.min(max, x));
  }
  
  public double getTrimmed() {
    return trim(get());
  }
  
  
 
  public ParameterMeta getParameterMeta() {
    return meta;
  }
 
  public double getRaw() {
    return value;
  }
  
  public double get() {
    return value;
  }
  
  public void set(double v) {
    value = v;
  }
  
  public  double  Ratio(double x) { return (x - min) / (max - min); }
  public  double  getBoxed()      { return Math.min(max, Math.max(min, get())); }
  public  double  getBoxedRatio() { return Ratio(getBoxed()); }
  
  
  public double  getBarX(double w) {
    if (zeroInside) {
      if (get() > 0.0)  return w * Ratio(0.0);
                  else  return w * getBoxedRatio();
    } else  return 0.0;
  }
  
  public double  getBarW(double w) {
    if (zeroInside) {
      if (get() > 0.0)  return w * getBoxed() / (max - min);
                  else  return w * getBoxed() / (min - max);
    } else  return w * getBoxedRatio();
  }
  

  
  public String valueAsString() {
    return util.format(get(), min, max, step);
  }
  
  // --------------------------------------------------------------------------

  /**
   * Creates a parameter.
   * @param meta  Provides information (name etc.) about the parent of the parameter.
   * @param key   The key in the properties file (for I18N).
   * @param unit  The physical unit (like &quot;ml/min&quot;)
   */
  public Parameter(ParameterMeta meta, String key, Unit unit, double min, double max, double step) {
    
    this.meta  = meta;
    this.key   = key;
    this.unit  = unit;
    this.min   = min;
    this.max   = max;
    this.step  = step;
    zeroInside = (min < 0.0) && (0.0 < max);
    scale = Scale.LINEAR;
    setOptimize(false);
  }
  

  // --------------------------------------------------------------------------
  /**
   * Internationalization support.
   * @param ext Find I18N string for <code>key + "_" + ext</code>.
   * @return The internationalized string.
   */
  public String I18N(String ext) {
    return Global.i18n(meta.getBlockInfoName() + "." + key + ext);
  }
  
  /**
   * Returns the short internationalized name.
   */
  
  public String getName() {
    return I18N("");
  }
  
  /**
   * Returns the long internationalized name.
   */
  public String getLongName() {
    return I18N("long");
  }
  
  public Unit getUnit() {
    return unit;
  }
  
  // ----- optimization -------------------------------------------------------
  
  private boolean optimize;
 
  
  /**
   * Sets whether this Parameter used for optimization.
   */
  public void setOptimize(boolean o) {
    optimize = o;
  }

  /**
   * Returns whether this Parameter used for optimization.
   */
  public boolean getOptimize() {
   return optimize;
  }

  // --------------------------------------------------------------------------
  /**
   * Can be overridden.
   */
  public void compute() {}
  
  /**
   * Can be overridden by subclass.
   */
  public void valueChanged() { }


  // --------------------------------------------------------------------------
  
  public String valueAndUnit() {
    return valueAsString() + " " + getUnit().getUnit();
  }
  
  public String toString() {
    return meta.getName() + "#" + getName() + ": " + valueAndUnit();
  }

  public String toShortString() {
    return getName() + ": " + valueAndUnit();
  }
  
  
  public String toUrl() {
    return getParameterMeta().getBlockInfoName() + "#" + getKey();
  }
}
