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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;

/**
 *
 * @author esteban
 */
@PropertyReactive
public class ModelRecommendation {
    
    private DiseaseModel model;
    
    //is this enough? Could we have different recommendations coming from different
    //rules for the same parameter?
    private Map<JCpSimParameter, Double> recommendedValues = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
    
    //in the case where a safety rule overwrites the value of a recommendation,
    //we should keep both rules as the source of the change. That is why we use
    //a List<String> as the value of the map
    private Map<JCpSimParameter, List<String>> sources = new EnumMap<JCpSimParameter, List<String>>(JCpSimParameter.class);

    private Map<JCpSimParameter, ConstraintViolation> vetoedReasons = new EnumMap<JCpSimParameter, ConstraintViolation>(JCpSimParameter.class);
    
    public ModelRecommendation(DiseaseModel model) {
        this.model = model;
    }

    
    public Map<JCpSimParameter, Double> getValidRecommendedValues() {
        Map<JCpSimParameter, Double> validRecommendations = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);

        for (Map.Entry<JCpSimParameter, Double> entry : this.recommendedValues.entrySet()) {
            if (this.getVetoedReason(entry.getKey()) == null){
                validRecommendations.put(entry.getKey(), entry.getValue());
            }
        }
        
        return validRecommendations;
    }
    
    public Map<JCpSimParameter, Double> getVetoedRecommendedValues() {
        Map<JCpSimParameter, Double> vetoedRecommendations = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);

        for (Map.Entry<JCpSimParameter, Double> entry : this.recommendedValues.entrySet()) {
            if (this.getVetoedReason(entry.getKey()) != null){
                vetoedRecommendations.put(entry.getKey(), entry.getValue());
            }
        }
        
        return vetoedRecommendations;
    }

    public Map<JCpSimParameter, Double> getRecommendedValues() {
        return recommendedValues;
    }
    
    public Map<JCpSimParameter, List<String>> getRecommendationSources() {
        return sources;
    }
    
    public void setRecommendedValues(Map<JCpSimParameter, Double> recommendedValues) {
        this.recommendedValues = recommendedValues;
    }

    @Modifies({"recommendedValues", "notEmpty"})
    public void addRecommendation(JCpSimParameter parameter, Double value, String source){
        this.recommendedValues.put(parameter, value);
        
        List<String> existingSources = this.sources.get(parameter);
        if (existingSources == null){
            existingSources = new ArrayList<String>();
            this.sources.put(parameter, existingSources);
        }
        
        existingSources.add(source);
    }
    
    public boolean isNotEmpty(){
        return !this.recommendedValues.isEmpty();
    }
    
    public void setNotEmpty(boolean b){
    }
    
    

    public Map<JCpSimParameter, ConstraintViolation> getVetoedReasons() {
        return vetoedReasons;
    }
    
    public ConstraintViolation getVetoedReason(JCpSimParameter parameter) {
        return vetoedReasons.get(parameter);
    }

    public void setVetoedReasons(Map<JCpSimParameter, ConstraintViolation> vetoedReasons) {
        this.vetoedReasons.putAll(vetoedReasons);
    }
    
    @Modifies({"vetoedReasons", "vetoed"})
    public void addVetoedReason(JCpSimParameter parameter, ConstraintViolation constraintViolation){
        this.vetoedReasons.put(parameter, constraintViolation);
    }
    
    public boolean isVetoed() {
        return !vetoedReasons.isEmpty();
    }
    
    public void setVetoed(boolean b){
    }
    
    
    @Modifies({"recommendedValues", "vetoedReasons", "notEmpty", "vetoed"})
    public void clear(){
        this.recommendedValues.clear();
        this.vetoedReasons.clear();
        this.sources.clear();
    }
    
    
    
    public DiseaseModel getModel() {
        return model;
    }
    
    public void setModel(DiseaseModel model) {
        this.model = model;
    }
    
}
