/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
