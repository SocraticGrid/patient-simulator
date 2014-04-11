/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.expectation;

import com.cognitive.nsf.management.model.ModelSessionManager;

/**
 *
 * @author esteban
 */
public abstract class PolicyEnforcer {
    private ModelSessionManager modelSessionManager;
    
    public void configure(ModelSessionManager modelSessionManager){
        this.modelSessionManager = modelSessionManager;
    }

    public ModelSessionManager getModelSessionManager() {
        return modelSessionManager;
    }
    
    public abstract void onExpectationResults(ExpectationResults results);
    
}
