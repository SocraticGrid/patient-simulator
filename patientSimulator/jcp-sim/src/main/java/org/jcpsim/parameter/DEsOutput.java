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
