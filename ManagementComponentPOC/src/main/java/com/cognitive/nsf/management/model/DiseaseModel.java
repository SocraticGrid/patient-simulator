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
