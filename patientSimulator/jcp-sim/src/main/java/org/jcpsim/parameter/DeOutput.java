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
