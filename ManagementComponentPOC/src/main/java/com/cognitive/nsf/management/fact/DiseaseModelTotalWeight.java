/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.fact;

import com.cognitive.nsf.management.model.DiseaseModel;

/**
 *
 * @author esteban
 */
public class DiseaseModelTotalWeight {
    private DiseaseModel model;
    private double totalWeight;

    public DiseaseModelTotalWeight() {
    }

    public DiseaseModelTotalWeight(DiseaseModel model, double totalWeight) {
        this.model = model;
        this.totalWeight = totalWeight;
    }

    public DiseaseModel getModel() {
        return model;
    }

    public void setModel(DiseaseModel model) {
        this.model = model;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }
    
}
