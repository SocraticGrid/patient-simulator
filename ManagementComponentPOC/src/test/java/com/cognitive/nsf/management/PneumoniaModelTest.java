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
    public void doTest() throws Exception{
        manager.startGatheringData();
        
        
        Thread.sleep(120000);
    }
    
    
}
