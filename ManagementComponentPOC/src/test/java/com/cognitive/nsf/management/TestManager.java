/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.management.model.DiseaseModel;
import java.util.List;
import org.jcpsim.data.JCpSimDataManager;

/**
 *
 * @author esteban
 */
public class TestManager extends Manager {

    public TestManager(List<DiseaseModel> models, JCpSimDataManager dataManager, long sampleRate) {
        super(models, dataManager, sampleRate);
        this.createAndConfigureKSession();
    }

    public TestManager(List<DiseaseModel> models, JCpSimDataManager dataManager, JCpSimDataGatherer dataGatherer) {
        super(models, dataManager, dataGatherer);
        this.createAndConfigureKSession();
    }

    public void insertFact(Object fact){
        this.ksession.insert(fact);
        this.ksession.fireAllRules();
    }
    
    
    
}
