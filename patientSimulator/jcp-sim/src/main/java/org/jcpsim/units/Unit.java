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
