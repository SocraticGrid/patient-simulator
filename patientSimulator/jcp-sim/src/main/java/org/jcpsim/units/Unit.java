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
package org.jcpsim.units;

public class Unit {
  
  public static Unit V_l           = new Unit("Volume",     "l");
  public static Unit t_s           = new Unit("Time",       "s");
  public static Unit t_min         = new Unit("Time",       "min");
  public static Unit P_mbar        = new Unit("Pressure",   "mbar");
  public static Unit P_mmHg        = new Unit("Pressure",   "mmHg");
  public static Unit F_ml_s        = new Unit("Flow",       "ml/s");
  public static Unit F_l_s         = new Unit("Flow",       "l/s");
  public static Unit m_mg          = new Unit("Mass",       "mg");
  public static Unit m_kg          = new Unit("Mass",       "kg");
  public static Unit no_unit       = new Unit("",           "");
  public static Unit f_min         = new Unit("Frequency",  "/min");
  public static Unit f_Hz          = new Unit("Frequency",  "Hz");
  public static Unit _perc         = new Unit("",           "%");
  public static Unit r_mg_h        = new Unit("Rate",       "mg/h");
  public static Unit R_mbar_l_s    = new Unit("Resistance", "mbar/(l/s)");
  public static Unit R_mmHg_ml_s   = new Unit("Resistance", "mmHg/(ml/s)");
  public static Unit C_l_mbar      = new Unit("Compliance", "l/mbar");
  public static Unit C_ml_mmHg     = new Unit("Compliance", "ml/mmHg");
  public static Unit L_ss_mmHg_ml  = new Unit("Inertance",  "mmHg/(ml/s²)");
  
  public static Unit V_mls_min     = new Unit("Volume", "mls/min");
  public static Unit F_l_m         = new Unit("Flow", "l/min");
  public static Unit CO_gm_dl      = new Unit("Concentration", "gm/dl");
  public static Unit T_c      = new Unit("Temperature", "ºC");
  public static Unit cc_kg      = new Unit("volume", "cc/kg");

  
  private String name;
  private String unit;
  
  
  public Unit(String name, String unit) {
    this.name = name;
    this.unit = unit;
  }

  public String getUnit()  { return unit; }
  public String getName()  { return name; }
  public String toString() { return name + " [" + unit + "]"; }
}
