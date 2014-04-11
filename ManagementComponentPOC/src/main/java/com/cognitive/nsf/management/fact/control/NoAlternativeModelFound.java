/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
