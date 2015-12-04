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
