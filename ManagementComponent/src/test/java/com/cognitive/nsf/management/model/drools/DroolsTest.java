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
package com.cognitive.nsf.management.model.drools;

import com.cognitive.nsf.management.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.management.model.Model;
import com.cognitive.nsf.management.model.ModelSessionManager;
import com.cognitive.nsf.management.model.RangedSingleParameterModel;
import com.cognitive.nsf.management.model.expectation.ConstantWeightExpectationEvaluator;
import com.cognitive.nsf.management.model.expectation.ExpectationResults;
import com.cognitive.nsf.management.model.expectation.MinimumWeightPolicyEnforcer;
import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluator;
import com.cognitive.nsf.management.model.expectation.PolicyEnforcer;
import com.cognitive.nsf.management.model.expectation.drools.DroolsModelExpectationEvaluator;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;

/**
 *
 * @author esteban
 */
public class DroolsTest {
 
    @Test
    public void testEmptyModel() throws Exception{
        DroolsModel model = new DroolsModel.Factory()
                .addResource(ResourceFactory.newClassPathResource("com/cognitive/nsf/management/model/drools/DroolsTestKillPatientAfter1000SamplesModelRules.drl"), ResourceType.DRL)
                .createDroolsModelInstance();
        
        DroolsModelExpectationEvaluator expectationEvaluator = new DroolsModelExpectationEvaluator.Factory(model)
                .addResource(ResourceFactory.newClassPathResource("com/cognitive/nsf/management/model/drools/DroolsTestModelExpectationEvaluatorRules.drl"), ResourceType.DRL)
                .createDroolsModelExpectationEvaluatorInstance();
        
        
        PolicyEnforcer policyEnforcer = new MinimumWeightPolicyEnforcer();
        
        JCpSimPollingClient client = new JCpSimPollingClient(Global.MODE.SIM.getJMXUrl());
        
        ModelSessionManager sessionManager = new ModelSessionManager(client, policyEnforcer);
        sessionManager.addModel(model, true);
        sessionManager.addModelExpectationEvaluator(expectationEvaluator);
        
        //static model
        Model complianceRangedModel = new RangedSingleParameterModel(JCpSimParameter.P_COMPLIANCE, 0.04, 0.1, 0.01);
        ModelExpectationEvaluator complianceRangedModelExpectationEvaluator = new ConstantWeightExpectationEvaluator(ExpectationResults.ConstraintViolation.TYPE.SOFT, 1, complianceRangedModel);
        sessionManager.addModel(complianceRangedModel);
        sessionManager.addModelExpectationEvaluator(complianceRangedModelExpectationEvaluator);
        
        
        JCpSimDataGatherer g = new JCpSimDataGatherer(client);
        g.addEventListener(sessionManager);

        g.start();
        Thread.sleep(60000);
        g.stop();
        
        
    }
    
}
