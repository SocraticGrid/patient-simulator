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
package com.cognitive.bp.poc.recommendation;

import java.util.LinkedHashMap;
import java.util.Map;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

/**
 *
 * @author esteban
 */
public class RecommendationSystemFactory {
    
    private boolean debugEnabled;
    private final Map<Resource, ResourceType> extraResources = new LinkedHashMap<>();
    
    
    public RecommendationSystemFactory setDebugEnabled(boolean enabled){
        this.debugEnabled = enabled;
        return this;
    }
 
    public RecommendationSystemFactory addExtraResource(Resource resource, ResourceType resourceType){
        this.extraResources.put(resource, resourceType);
        return this;
    }
    
    public RecommendationSystem build(){
        
        RecommendationSystem recommendationSystem = new RecommendationSystem();
        
        recommendationSystem.setDebugEnabled(this.debugEnabled);
        for (Map.Entry<Resource, ResourceType> entry : this.extraResources.entrySet()) {
            recommendationSystem.addExtraResource(entry.getKey(), entry.getValue());
        }
        
        recommendationSystem.init();
        return recommendationSystem;
    }
    
    
    
    
}
