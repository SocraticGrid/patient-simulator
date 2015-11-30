/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.management.model.DiseaseModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jcpsim.data.JCpSimDataManager;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;


public class ManagerBuilder {
    private List<DiseaseModel> models;
    private JCpSimDataManager dataManager;
    private JCpSimDataGatherer dataGatherer;
    private Long sampleRate;
    private Map<Resource, ResourceType> internalResources = new LinkedHashMap<Resource, ResourceType>();
    private Map<String, Object> internalGlobals = new HashMap<String, Object>();
    private List<AgendaEventListener> agendaEventListeners = new ArrayList<AgendaEventListener>();
    private List<RuleRuntimeEventListener> workingMemoryEventListeners = new ArrayList<RuleRuntimeEventListener>();
    private ManagerEventListener eventListener;
    private double threshold = 0.0;

    public ManagerBuilder() {
    }

    public ManagerBuilder setModels(List<DiseaseModel> models) {
        this.models = models;
        return this;
    }

    public ManagerBuilder setDataManager(JCpSimDataManager dataManager) {
        this.dataManager = dataManager;
        return this;
    }

    public ManagerBuilder setDataGatherer(JCpSimDataGatherer dataGatherer) {
        this.dataGatherer = dataGatherer;
        return this;
    }
    
    public ManagerBuilder setSampleRate(Long sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }
    
    public ManagerBuilder setEventListener(ManagerEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }
    
    public ManagerBuilder addExtraResource(Resource resource, ResourceType type) {
        this.internalResources.put(resource, type);
        return this;
    }

    public ManagerBuilder registerGlobal(String key, Object value) {
        internalGlobals.put(key, value);
        return this;
    }

    public ManagerBuilder addAgendaEventListener(AgendaEventListener agendaEventListener) {
        agendaEventListeners.add(agendaEventListener);
        return this;
    }

    public ManagerBuilder addWorkingMemoryEventListener(RuleRuntimeEventListener workingMemoryEventListener) {
        workingMemoryEventListeners.add(workingMemoryEventListener);
        return this;
    }

    public ManagerBuilder setThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }
    
    public Manager createManager() {
        Manager manager;
        
        if (dataGatherer == null){
            manager = new Manager(models, dataManager, sampleRate);
        }else{
            manager = new Manager(models, dataManager, dataGatherer);
        }

        for (Map.Entry<Resource, ResourceType> entry : this.internalResources.entrySet()) {
            manager.addExtraResource(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, Object> entry : this.internalGlobals.entrySet()) {
            manager.registerGlobal(entry.getKey(), entry.getValue());
        }
        
        for (AgendaEventListener agendaEventListener : this.agendaEventListeners) {
            manager.addAgendaEventListener(agendaEventListener);
        }
        
        for (RuleRuntimeEventListener workingMemoryEventListener : this.workingMemoryEventListeners) {
            manager.addWorkingMemoryEventListener(workingMemoryEventListener);
        }
        
        manager.setThreshold(threshold);
         
        manager.createAndConfigureKSession();
        manager.setEventListener(eventListener);
        
        return manager;
    }
    
}
