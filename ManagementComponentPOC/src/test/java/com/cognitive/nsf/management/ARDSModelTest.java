/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.management.model.ARDSModel;
import com.cognitive.nsf.management.model.DiseaseModel;
import java.util.ArrayList;
import java.util.List;
import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class ARDSModelTest {
    
    private ARDSModel ardsModel;
    private JCpSimDataManager client;
    private JCpSimDataGatherer dataGatherer;
    private TestManager manager;
    
    @Before
    public void setUp() throws Exception{
        List<DiseaseModel> models = new ArrayList<DiseaseModel>();
        
        
        ardsModel = new ARDSModel();
        ardsModel.setActive(true);
        
        models.add(ardsModel);
        
        client = new JCpSimPollingClient(Global.MODE.SIM.getJMXUrl());
        
        dataGatherer = new JCpSimDataGatherer(client);
        manager = new TestManager(models, client, dataGatherer);
    }
    
    @Test
    public void doTest() throws Exception{
        manager.startGatheringData();
        
        
        Thread.sleep(120000);
    }
    
    
}
