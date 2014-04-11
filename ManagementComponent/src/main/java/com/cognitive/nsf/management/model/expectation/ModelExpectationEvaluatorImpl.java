/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.expectation;

import com.cognitive.nsf.management.model.Model;
import org.jcpsim.data.JCpSimData;


public class ModelExpectationEvaluatorImpl implements ModelExpectationEvaluator {
    private final Model model;
    private PolicyEnforcer policyEnforcer;

    public ModelExpectationEvaluatorImpl(Model model) {
        this.model = model;
    }

    public void configure(PolicyEnforcer policyEnforcer) {
        this.policyEnforcer = policyEnforcer;
    }

    public void processData(JCpSimData data) {
        //do nothing
    }

    public PolicyEnforcer getPolicyEnforcer() {
        return policyEnforcer;
    }

    public void setPolicyEnforcer(PolicyEnforcer policyEnforcer) {
        this.policyEnforcer = policyEnforcer;
    }

    public Model getModel() {
        return model;
    }
    
}
