/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import java.util.Date;
import java.util.Map;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author esteban
 */
public interface DiseaseModel {
    public String getName();
    public int getSalience();
    public void setActive(boolean active);
    public boolean isActive();
    public Map<Resource, ResourceType> getResources();
    public void setGlobals(StatefulKnowledgeSession ksession);
    public void insertInitialFacts(StatefulKnowledgeSession ksession);
    public void setLastActivationDate(Date lastActivationDate);
    public Date getLastActivationDate();
    
    
}
