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
package com.cognitive.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esteban
 */
public class Delta {
    private final String parameter;

    private final Map<String,Double> values = new HashMap<String, Double>();
    
    public Delta(String parameter) {
        this.parameter = parameter;
    }
    
    public synchronized void setValue(String source, double value){
        this.values.put(source, value);
    }
    
    public synchronized void addValue(String source, double value){
        Double previousValue = this.values.get(source);
        
        if (previousValue != null){
            value += previousValue;
        }
        
        this.setValue(source, value);
    }
    
    public double getValue(String source){
        return this.getValue(source);
    }
    
    public synchronized double getTotal(){
        double result = 0;
        
        for (Double value : this.values.values()) {
            result += value;
        }
        
        return result;
    }

    public String getParameter() {
        return parameter;
    }
    
}
