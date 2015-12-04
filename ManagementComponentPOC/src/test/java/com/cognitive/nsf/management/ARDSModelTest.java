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
 * These tests must be executed while JCPSim is running and its Custom Respirator
 * Scenario is active.
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
