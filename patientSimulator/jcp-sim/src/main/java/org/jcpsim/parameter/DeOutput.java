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


import org.jcpsim.ode.DifferentialEquation;
import org.jcpsim.units.Unit;


/*
 * Uses the result of a DifferentialEquation as Output.
 */
public class DeOutput extends Output {
  
  private DifferentialEquation de;
  
  public DeOutput(ParameterMeta meta, String key, Unit unit, 
                  double min, double max, double step, DifferentialEquation de) {
    super(meta, key, unit, min, max, step);
    this.de = de;
  }
  
  
  public double get() {
    return de.get();
  }
}
