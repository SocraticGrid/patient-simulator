/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;

public abstract class AbstractModel implements DiseaseModel {

    private String name;
    private boolean active;
    private int salience;
    private Date lastActivationDate;
    private Map<Resource, ResourceType> resources = new LinkedHashMap<Resource, ResourceType>();

    public AbstractModel(String name, int salience) {
        this.name = name;
        this.salience = salience;
    }
    
    public AbstractModel(String name, int salience, Map<Resource, ResourceType> resources) {
        this(name, salience);
        this.setResources(resources);
        
    }

    public String getName() {
        return this.name;
    }

    public final void setResources(Map<Resource, ResourceType> resources){
        this.resources.putAll(resources);
    }
    
    public Map<Resource, ResourceType> getResources(){
        return this.resources;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getSalience() {
        return salience;
    }

    public Date getLastActivationDate() {
        return lastActivationDate;
    }

    public void setLastActivationDate(Date lastActivationDate) {
        this.lastActivationDate = lastActivationDate;
    }
    
}
