/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.model.ARDSModel;
import com.cognitive.nsf.management.model.DiseaseModel;
import com.cognitive.nsf.management.model.PneumoniaModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class PneumoniaModelTest {
    
    private MockJCpSimDataManager recommendationClient;
    private PneumoniaModel pneumoniaModel;
    private MockJCpSimDataManager client;
    private MockJCpSimDataGatherer dataGatherer;
    private TestManager manager;
    
    @Before
    public void setUp() throws Exception{
        List<DiseaseModel> models = new ArrayList<DiseaseModel>();
        
        recommendationClient = new MockJCpSimDataManager();
        
        
        pneumoniaModel = new PneumoniaModel();
        pneumoniaModel.setActive(true);
        
        models.add(pneumoniaModel);
        
        client = new MockJCpSimDataManager();
        dataGatherer = new MockJCpSimDataGatherer(client);
        manager = new TestManager(models, client, dataGatherer);
    }
    
    @Test
    public void doTest(){
        
    }
    
    
}
