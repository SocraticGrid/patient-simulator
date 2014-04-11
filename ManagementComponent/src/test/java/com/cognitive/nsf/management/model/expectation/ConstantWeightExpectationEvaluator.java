/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.expectation;

import com.cognitive.nsf.management.model.Model;
import com.cognitive.nsf.management.model.expectation.ExpectationResults.ConstraintViolation.TYPE;
import org.jcpsim.data.JCpSimData;

/**
 *
 * @author esteban
 */
public class ConstantWeightExpectationEvaluator extends ModelExpectationEvaluatorImpl{

    private final double weight;
    private final ExpectationResults.ConstraintViolation.TYPE type;

    public ConstantWeightExpectationEvaluator(TYPE type, double weight, Model model) {
        super(model);
        this.weight = weight;
        this.type = type;
    }
    
    @Override
    public void processData(JCpSimData data) {
        ExpectationResults expectationResults = new ExpectationResults(this.getModel());
        expectationResults.addViolation(type, weight);
        
        this.getPolicyEnforcer().onExpectationResults(expectationResults);
    }
    
    
}
