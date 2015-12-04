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
package com.cognitive.nsf.management.fact.control;

import com.cognitive.nsf.management.model.DiseaseModel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esteban
 */
public class NoAlternativeModelFound {
    private DiseaseModel lastModel;
    private Map<String, Double> modelWeights = new HashMap<String, Double>();

    public NoAlternativeModelFound(DiseaseModel lastModel, Map<String, Double> modelWeights) {
        this.lastModel = lastModel;
        if (modelWeights != null){
            this.modelWeights.putAll(modelWeights);
        }
    }

    public Double put(String modelName, Double weight) {
        return modelWeights.put(modelName, weight);
    }

    public Map<String, Double> getModelWeights() {
        return modelWeights;
    }

    public DiseaseModel getLastModel() {
        return lastModel;
    }
    
}
