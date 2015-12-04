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

import java.util.logging.Logger;

/**
 * A  Parameter that can be set by the user.
 *     <ul>
 *     <li>THERAPY : influences the results (PEEP)</li>
 *     <li>PATIENT : influences the results, but can not be changed
 *                   for therapy (compliance of left lung)</li>
 *     <li>NOEFFECT: does not influence the results (brightness of ventilator
                     display)</li>
 *     <li>CONTROL : variable outside of the simulation scenario 
 *                   (simulation speed)</li>
 *     </ul>
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 * TODO:    binary representation with BitSet
 */
 public class Input extends Parameter {
   
   private static final Logger logger = Logger.getLogger(Input.class.getName());

   public double            def;        // default value
   public Parameter.Scale   scale;      // LINEAR or EXPONENTIAL
// public boolean           active;
   
   public double  getDefault()    { return def;     }
   public void    setToDefault()  { setDouble(def); }
   
   // --------------------------------------------------------------------------
   
   public int     i;          // actual integer value
   public int     maxint;     // i is element of [0..maxint]
   public int     bits;       // number of bits needed for representation of i

   
   public double iToD(int i) {
     switch(scale) {
       case LINEAR     :  return  getStep() * i + getMin();
       case EXPONENTIAL:  return  Math.pow(getStep(), i) * getMin();
     }
     return 0.0;
   }


   public int dToI(double d) {
     switch(scale) {
       case LINEAR     :  return  (int)((d - getMin()) / getStep());
       case EXPONENTIAL:  return  (int)(Math.log(d / getMin()) / Math.log(getStep()));
     }
     return 0;
   }

   // --------------------------------------------------------------------------
   
  public Input(ParameterMeta meta, String name, Unit unit,
                        double min, double def, double max, double step,
                        Parameter.Scale sc) {
                 
    super(meta, name, unit, min, max, step);
    this.def   = def;
    scale      = sc;

    maxint     = dToI(max);
    bits       = (int)Math.ceil(Math.log(maxint) / Math.log(2));
    setToDefault();
  }


  public Input(ParameterMeta meta, String name, Unit unit,
               double min, double def, double max, double step) {
    this(meta, name, unit, min, def, max, step, Parameter.Scale.LINEAR);
  }

  public Input(ParameterMeta meta, String name, Unit unit,
               double min, double def, double max) {
    this(meta, name, unit, min, def, max, 1.0, Parameter.Scale.LINEAR);
  }


  public Input(ParameterMeta meta, String name, Unit unit,
               double def, double max) {
    this(meta, name, unit, 0.0, def, max, 1.0, Parameter.Scale.LINEAR);
  }


  public Input(ParameterMeta meta, String name, Unit unit, double def) {
    this(meta, name, unit, 0.0, def, 1.0, 1.0, Parameter.Scale.LINEAR);
  }

  public Input(ParameterMeta meta, String name, Unit unit) {
    this(meta, name, unit, 0.0, 0.0, 1.0, 1.0, Parameter.Scale.LINEAR);
  }

  // --------------------------------------------------------------------------
  
  public void setInteger(int x) {
    i = Math.max(0, Math.min(maxint, x));
    set(iToD(i));
    valueChanged();
  }

  public void setDouble(double v) {
    set(v);
    i = dToI(v);
    valueChanged();
  }
  
  // --------------------------------------------------------------------------

  public void add(int x) { 
    setInteger(i+x); 
  }
  
  public void addCyclic(int x) {
    setInteger((i+x+ 10*(maxint+1)) % (maxint+1)); 
  }
  
  public void addProportional(double i) {
    add((int)(i*maxint));
  }
  
  // --------------------------------------------------------------------------
//  import org.w3c.dom.Element;
//  import org.w3c.dom.Document;
  /**
   * Imports Parameter from an XML configuration document.
   */
/*  
  public void readElement(Element el) {
    id       = Integer.parseInt(el.getAttribute("id"));
    name     = el.getAttribute("name");
    longName = el.getAttribute("longname"  );
    unit     = el.getAttribute("unit");
    min      = Double.parseDouble(el.getAttribute("min"));
    def      = Double.parseDouble(el.getAttribute("def"));
    max      = Double.parseDouble(el.getAttribute("max"));
    step     = Double.parseDouble(el.getAttribute("step"));
    scale    = Scale.find(Integer.parseInt(el.getAttribute("scale")));
  }
*/  
  /**
   * Exports Parameter to an XML configuration document.
   */
/*  
  public Element createElement(Document doc) {
    Element el = doc.createElement("given");
    el.setAttribute("id",       Integer.toString(id));
    el.setAttribute("name",     name);
    el.setAttribute("longname", longName);
    el.setAttribute("unit",     unit);
    el.setAttribute("min",      Double.toString(min));
    el.setAttribute("def",      Double.toString(def));
    el.setAttribute("max",      Double.toString(max));
    el.setAttribute("step",     Double.toString(step));
    el.setAttribute("scale",    Integer.toString(scale.value()));
    return el;
  }
*/  
  // --------------------------------------------------------------------------

  public enum Scale {
    LINEAR,        // x[n+1] = x[n] + d
    EXPONENTIAL;   // x[n+1] = x[n] * d
    
    public int value() { return ordinal(); }
    
    public static Scale find(int v) {
      for (Scale s:Scale.values()) {
        if (s.value() == v)  return s;
      }
      return null;
    }
  }

  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  
  // --------------------------------------------------------------------------
/*
    step          = (max - min) / 1000.0;  // *****************************
  // --------------------------------------------------------------------------

  public void setValues(int min, int def, int max, String unit) {
    this.min   = min;
    this.def   = def;
    this.max   = max;
    this.unit  = unit;
  }


  public void setValues(double min, double def, double max, double step,
                        String unit) {
    this.min   = min;
    this.def   = def;
    this.max   = max;
    this.step  = step;
    this.unit  = unit;
  }

  public void setValues(double min, double def, double max, double step) {
    this.min   = min;
    this.def   = def;
    this.max   = max;
    this.step  = step;
  }

  public void setValues(double min, double def, double max) {
    setValues(min, def, max, 1.0);
  }

  public void setValues(double def, double max) {
    setValues(0.0, def, max, 1.0);
  }

  public void setValues(double def) {
    setValues(0.0, def, 1.0, 1.0);
  }

  public void setValues() {
    setValues(0.0, 0.0, 1.0, 1.0);
  }
*/
  // --------------------------------------------------------------------------
  
}
