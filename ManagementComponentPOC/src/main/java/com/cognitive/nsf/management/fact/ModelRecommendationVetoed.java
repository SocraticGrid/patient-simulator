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
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ModelRecommendationVetoed {
    private DiseaseModel model;
    private Map<JCpSimParameter, Double> vetoedChanges;
    private Map<JCpSimParameter, ConstraintViolation> vetoedReasons;

    public ModelRecommendationVetoed(DiseaseModel model, Map<JCpSimParameter, Double> vetoedChanges, Map<JCpSimParameter, ConstraintViolation> vetoedReasons) {
        this.model = model;
        
        this.vetoedChanges = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
        this.vetoedChanges.putAll(vetoedChanges);
        
        this.vetoedReasons = new EnumMap<JCpSimParameter, ConstraintViolation>(JCpSimParameter.class);
        this.vetoedReasons.putAll(vetoedReasons);
        
    }

    public Map<JCpSimParameter, Double> getVetoedChanges() {
        return vetoedChanges;
    }

    public Map<JCpSimParameter, ConstraintViolation> getVetoedReasons() {
        return vetoedReasons;
    }
    
    public DiseaseModel getModel() {
        return model;
    }
    
}
