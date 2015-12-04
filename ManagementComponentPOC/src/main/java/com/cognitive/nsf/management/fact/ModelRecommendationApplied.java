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

import com.cognitive.nsf.management.model.DiseaseModel;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ModelRecommendationApplied {
    private DiseaseModel model;
    private Map<JCpSimParameter, Double> appliedChanges;
    private Map<JCpSimParameter, List<String>> sources;

    public ModelRecommendationApplied(DiseaseModel model, Map<JCpSimParameter, Double> appliedChanges, Map<JCpSimParameter, List<String>> sources) {
        this.model = model;
        
        this.appliedChanges = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
        this.sources = new EnumMap<JCpSimParameter, List<String>>(JCpSimParameter.class);
        this.appliedChanges.putAll(appliedChanges);
        this.sources.putAll(sources);
        
    }

    public Map<JCpSimParameter, Double> getAppliedChanges() {
        return appliedChanges;
    }

    public Map<JCpSimParameter, List<String>> getSources() {
        return sources;
    }

    public DiseaseModel getModel() {
        return model;
    }
    
}
