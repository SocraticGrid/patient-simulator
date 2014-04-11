/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.drools;

import com.cognitive.nsf.management.model.expectation.drools.DroolsModelExpectationEvaluator;
import com.cognitive.nsf.management.model.expectation.drools.DroolsPolicyEnforcer;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class DroolsBasicTests {
 
    @Test
    public void testEmptyModel(){
        DroolsModel model = new DroolsModel.Factory().createDroolsModelInstance();
    }
    
    @Test
    public void testEmptyModelExpectationEvaluator(){
        DroolsModel model = new DroolsModel.Factory().createDroolsModelInstance();
        new DroolsModelExpectationEvaluator.Factory(model).createDroolsModelExpectationEvaluatorInstance();
    }
    
    @Test
    public void testEmptyPolicyEnforcer(){
        new DroolsPolicyEnforcer.Factory().createDroolsPolicyEnforcerInstance();
    }
    
}
