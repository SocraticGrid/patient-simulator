/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
