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
package com.cognitive.nsf.management.model;

import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluator;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class RangedSingleParameterModel implements Model{

    private double delta;
    private final JCpSimParameter parameter;
    private final double min;
    private final double max;
    
    private ModelSessionManager manager;

    public RangedSingleParameterModel(JCpSimParameter parameter, double min, double max, double delta) {
        this.parameter = parameter;
        this.min = min;
        this.max = max;
        this.delta = delta;
    }
    
    public void init(ModelSessionManager manager) {
        this.manager = manager;
    }

    public void processData(JCpSimData data) {
        JCpSimData output = this.clone(data);
        
        double currentValue = data.get(parameter);
        
        if ( Double.compare(currentValue, min) <= 0){
            delta = Math.abs(delta);
        } else if (Double.compare(currentValue, max) >= 0){
            delta = Math.abs(delta)*-1;
        }
        
        output.set(parameter, currentValue + delta);
        
        manager.onResult(this, output);
    }
    
    private JCpSimData clone(JCpSimData original){
        JCpSimData clone = new JCpSimDataImpl();
        for (JCpSimParameter jCpSimParameter : JCpSimParameter.values()) {
            clone.set(jCpSimParameter, original.get(jCpSimParameter));
        }
        return clone;
    }

    @Override
    public String toString() {
        return "RangedSingleParameterModel{" + "delta=" + delta + ", parameter=" + parameter + ", min=" + min + ", max=" + max + '}';
    }
    

}
