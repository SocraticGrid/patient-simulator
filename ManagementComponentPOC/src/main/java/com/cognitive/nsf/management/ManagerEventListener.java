/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.fact.control.DiseaseActionRequested;
import com.cognitive.nsf.management.model.DiseaseModel;

/**
 *
 * @author esteban
 */
public interface ManagerEventListener {
    void onModelChanged(DiseaseModel newModel);
    void onDiseaseActionRequested(DiseaseActionRequested actionRequest);
}
