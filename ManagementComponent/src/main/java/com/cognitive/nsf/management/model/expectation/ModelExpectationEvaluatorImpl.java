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
