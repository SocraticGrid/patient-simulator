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
package com.cognitive.nsf.management.model.expectation;

import com.cognitive.nsf.management.model.Model;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esteban
 */
public class MinimumWeightPolicyEnforcer extends PolicyEnforcer{

    private Map<Model, Double> modelWeights = new HashMap<Model, Double>();
    private Model lastSuggestedModel;
    private Double minimumWeight;
    
    public synchronized void onExpectationResults(ExpectationResults results) {
        double totalWeight = results.getHardTotalWeight()*2 + results.getSoftTotalWeight();
        modelWeights.put(results.getModel(), totalWeight);

        if (results.getModel() == lastSuggestedModel && totalWeight == minimumWeight){
            return;
        }
        
        //Get the minimum weight from all the models
        Map.Entry<Model, Double> minimumEntry = null;
        for (Map.Entry<Model, Double> entry : modelWeights.entrySet()) {
            if (minimumEntry == null || entry.getValue() < minimumEntry.getValue()){
                minimumEntry = entry;
            }
        }
        
        if (minimumEntry != null && minimumEntry.getKey() != lastSuggestedModel){
            lastSuggestedModel = minimumEntry.getKey();
            minimumWeight = minimumEntry.getValue();
            
            System.out.println("\n\n--------- Policy Enforcer ---------");
            for (Map.Entry<Model, Double> entry : modelWeights.entrySet()) {
                System.out.print("["+entry.getKey()+"] -> "+entry.getValue());
                if (entry.getKey() == minimumEntry.getKey()){
                    System.out.print("  <---[Active]");
                }
                System.out.println("");
            }
            System.out.println("");
            
            System.out.println("Swithced to model "+minimumEntry.getKey()+": "+minimumEntry.getValue());
            this.getModelSessionManager().setActiveModel(minimumEntry.getKey());
        }
        
    }
    
    
}
