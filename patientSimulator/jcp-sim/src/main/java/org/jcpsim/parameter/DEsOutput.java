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
import org.jcpsim.ode.DifferentialEquations;
import org.jcpsim.ode.DifferentialEquations.Method;
import org.jcpsim.scenarios.Block;
import org.jcpsim.units.Unit;


/*
 * Output of the common independent variable (usually time) of some ODEs.
 */
public class DEsOutput extends Output {
  
  private DifferentialEquations des;
  private Block                 block;
  
  public DEsOutput(Block block, double t, double dt, Method method,
                   String key, Unit unit, double min, double max, double step) {
    
    super(block, key, unit, min, max, step);
    des = new DifferentialEquations(t, dt, method);
    this.block = block;
  }
  
  public DeOutput add(String key, Unit unit, 
                      double min, double max, double step, DifferentialEquation de) {
    DeOutput deo = new DeOutput(block, key, unit, min, max, step, de);
    des.add(de);
    block.addO(deo);
    return deo;
  }
  
  public void stepDelta(double delta) {
    des.stepDelta(delta);
  }
  
  public double get() {
    return des.getT();
  }
  
  public void setT(double value){
      this.des.setT(value);
  }
}
