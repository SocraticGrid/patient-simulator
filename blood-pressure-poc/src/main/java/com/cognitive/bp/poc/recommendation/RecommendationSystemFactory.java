/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
