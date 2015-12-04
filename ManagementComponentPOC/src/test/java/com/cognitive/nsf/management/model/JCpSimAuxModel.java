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
package com.cognitive.nsf.management.model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.jcpsim.data.JCpSimDataManager;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author esteban
 */
public class JCpSimAuxModel extends AbstractModel {

    
    private final JCpSimDataManager provider;
    
    public JCpSimAuxModel(String name, Map<Resource, ResourceType> resources, JCpSimDataManager dataManager) throws IOException, MalformedObjectNameException, InstanceNotFoundException {
        this(name, resources, dataManager, false);
    }
    
    public JCpSimAuxModel(String name, Map<Resource, ResourceType> resources, JCpSimDataManager dataManager, boolean excludeModelDrl) throws IOException, MalformedObjectNameException, InstanceNotFoundException {
        super(name, 0);
        this.provider = dataManager;
        
        Map<Resource, ResourceType> internalResources = new LinkedHashMap<Resource, ResourceType>();
        
        if (!excludeModelDrl){ 
            internalResources.put(ResourceFactory.newClassPathResource("rules/JCpSimModel.drl"), ResourceType.DRL);
        }
        internalResources.putAll(resources);
        
        this.setResources(internalResources);
    }

    public void setGlobals(KieSession ksession) {
        ksession.setGlobal("auxProvider", provider);
    }

    public void insertInitialFacts(KieSession ksession) {
    }
    
    
    
}
