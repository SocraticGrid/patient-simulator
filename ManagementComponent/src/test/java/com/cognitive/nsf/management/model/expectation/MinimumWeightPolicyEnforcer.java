/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
