/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import java.util.Date;
import java.util.Map;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;

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
    public void setGlobals(KieSession ksession);
    public void insertInitialFacts(KieSession ksession);
    public void setLastActivationDate(Date lastActivationDate);
    public Date getLastActivationDate();
    
    
}
