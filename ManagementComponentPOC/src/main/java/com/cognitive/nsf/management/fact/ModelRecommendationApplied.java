/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
