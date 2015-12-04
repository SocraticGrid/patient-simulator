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
package com.cognitive.nsf.management.fact;

import java.util.EnumMap;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

/**
 * Data containing all the parameter values at the time that the blood gas is calculated.
 * (Data when a gas is calculated no matter if there is a change or not in the 
 * resulting gas)
 * @author esteban
 */
public class GasCalculatedTimeJCpSimData {
    private Map<JCpSimParameter, Double> data = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
    private final long timestamp;

    
    public GasCalculatedTimeJCpSimData(Map<JCpSimParameter, Double> data) {
        this(data, System.currentTimeMillis());
    }
    
    public GasCalculatedTimeJCpSimData(Map<JCpSimParameter, Double> data, long timestamp) {
        this.data.putAll(data);
        this.timestamp = timestamp;
    }
    
    public Map<JCpSimParameter, Double> getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
}
