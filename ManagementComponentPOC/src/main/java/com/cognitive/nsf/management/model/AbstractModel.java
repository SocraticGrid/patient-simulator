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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

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
