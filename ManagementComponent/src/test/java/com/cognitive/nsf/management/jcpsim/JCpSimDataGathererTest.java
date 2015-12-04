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
package com.cognitive.nsf.management.jcpsim;

import com.cognitive.nsf.management.model.Model;
import com.cognitive.nsf.management.model.RangedSingleParameterModel;
import com.cognitive.nsf.management.model.ModelSessionManager;
import com.cognitive.nsf.management.model.expectation.ConstantWeightExpectationEvaluator;
import com.cognitive.nsf.management.model.expectation.ExpectationResults;
import com.cognitive.nsf.management.model.expectation.ExpectationResults.ConstraintViolation.TYPE;
import com.cognitive.nsf.management.model.expectation.MinimumWeightPolicyEnforcer;
import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluator;
import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluatorImpl;
import com.cognitive.nsf.management.model.expectation.PolicyEnforcer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.hamcrest.CoreMatchers.is;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests must be executed while JCPSim is running and the Custom Respirator
 * Scenario is active.
 * @author esteban
 */
public class JCpSimDataGathererTest {

    public static final String DEFAULT_JCP_SIM_CLIENT_JMX_URL = Global.MODE.SIM.getJMXUrl();
    private static JCpSimPollingClient client;
    
    private abstract class TestAsserter implements JCpSimDataReceivedEventListener{
        protected AtomicInteger numberOfFailedAssertions = new AtomicInteger(0);

        public int getNumberOfFailedAssertions() {
            return numberOfFailedAssertions.get();
        }
        
        public boolean hasFailedAssertions(){
            return this.getNumberOfFailedAssertions() != 0;
        }
        
    }
    
    private class ConstantParameterAsserter extends TestAsserter{

        private final JCpSimParameter parameter;
        private Double oldValue = null;

        public ConstantParameterAsserter(JCpSimParameter parameter) {
            this.parameter = parameter;
        }
        
        public void onDataReceived(JCpSimData data) {
            double newValue = data.get(parameter);

            if (oldValue != null) {
                try{
                    Assert.assertEquals(newValue, oldValue, 0.001);
                } catch (AssertionError e){
                    this.numberOfFailedAssertions.incrementAndGet();
                }
            }
            
            oldValue = newValue;
        }
        
    }
    
    private class VariableParameterAsserter extends TestAsserter{

        private final JCpSimParameter parameter;
        private Double oldValue = null;

        public VariableParameterAsserter(JCpSimParameter parameter) {
            this.parameter = parameter;
        }
        
        public void onDataReceived(JCpSimData data) {
            double newValue = data.get(parameter);

            
            if (oldValue != null) {
                if(Double.compare(newValue, oldValue) == 0){
                    this.numberOfFailedAssertions.incrementAndGet();
                }
            }
            
            oldValue = newValue;
            
        }
        
    }
    

    @BeforeClass
    public static void init() throws Exception {
        client = new JCpSimPollingClient(DEFAULT_JCP_SIM_CLIENT_JMX_URL);
    }

    @Test
    public void testJCpSimDataGatherer() throws Exception {

        final List<JCpSimData> collectedData = Collections.synchronizedList(new ArrayList<JCpSimData>());
        JCpSimDataGatherer g = new JCpSimDataGatherer(client);
        g.addEventListener(new JCpSimDataReceivedEventListener() {
            public void onDataReceived(JCpSimData data) {
                collectedData.add(data);
            }
        });

        g.start();
        Thread.sleep(10000);
        g.stop();

        Assert.assertFalse(collectedData.isEmpty());

    }

    @Test
    public void testModelSessionManager() throws Exception {

        Model complianceVariableModel = new RangedSingleParameterModel(JCpSimParameter.P_COMPLIANCE, 0.04, 0.12, 0.01);
        Model peepVariableModel = new RangedSingleParameterModel(JCpSimParameter.V_PEEP, 4, 6, 0.01);
        
        TestAsserter constantComplianceAsserter = new ConstantParameterAsserter(JCpSimParameter.P_COMPLIANCE);
        TestAsserter constantPEEPAsserter = new ConstantParameterAsserter(JCpSimParameter.V_PEEP);
        TestAsserter varialbeComplianceAsserter = new VariableParameterAsserter(JCpSimParameter.P_COMPLIANCE);
        TestAsserter varialbePEEPAsserter = new VariableParameterAsserter(JCpSimParameter.V_PEEP);
        
        PolicyEnforcer policyEnforcer = new MinimumWeightPolicyEnforcer();
        
        ModelSessionManager sessionManager = new ModelSessionManager(client, policyEnforcer);
        sessionManager.addModel(complianceVariableModel);
        sessionManager.addModel(peepVariableModel, true);

        JCpSimDataGatherer g = new JCpSimDataGatherer(client);
        g.addEventListener(sessionManager);
        g.addEventListener(varialbePEEPAsserter);
        g.addEventListener(constantComplianceAsserter);

        g.start();
        Thread.sleep(5000);
        g.stop();
        
        Assert.assertThat(varialbePEEPAsserter.hasFailedAssertions(), is(false));
        Assert.assertThat(constantComplianceAsserter.hasFailedAssertions(), is(false));
        
        g.removeEventListener(varialbePEEPAsserter);
        g.removeEventListener(constantComplianceAsserter);
        
        sessionManager.setActiveModel(complianceVariableModel);
        g.addEventListener(constantPEEPAsserter);
        g.addEventListener(varialbeComplianceAsserter);
        
        g.start();
        Thread.sleep(5000);
        g.stop();
        
        Assert.assertThat(constantPEEPAsserter.hasFailedAssertions(), is(false));
        Assert.assertThat(varialbeComplianceAsserter.hasFailedAssertions(), is(false));

    }
    
    @Test
    public void testModelExpectationEvaluator() throws Exception {

        Model complianceIncrementalModel = new RangedSingleParameterModel(JCpSimParameter.P_COMPLIANCE, 0.04, 100, 0.05);
        Model complianceRangedModel = new RangedSingleParameterModel(JCpSimParameter.P_COMPLIANCE, 0.04, 1.2, 0.01);
        
        ModelExpectationEvaluator complianceIncrementalModelExpectationEvaluator = new ModelExpectationEvaluatorImpl(complianceIncrementalModel){
            
            private boolean fired;
            
            @Override
            public void processData(JCpSimData data) {
                if (!fired && data.get(JCpSimParameter.P_COMPLIANCE) > 1){
                    fired = true;
                    ExpectationResults results = new ExpectationResults(this.getModel());
                    results.addViolation(TYPE.HARD, 999);
                    this.getPolicyEnforcer().onExpectationResults(results);
                }
            }
        };
        
        ModelExpectationEvaluator complianceRangedModelExpectationEvaluator = new ConstantWeightExpectationEvaluator(TYPE.SOFT, 1, complianceRangedModel);
        
        PolicyEnforcer policyEnforcer = new MinimumWeightPolicyEnforcer();
        
        ModelSessionManager sessionManager = new ModelSessionManager(client, policyEnforcer);
        sessionManager.addModel(complianceIncrementalModel, true);
        sessionManager.addModelExpectationEvaluator(complianceIncrementalModelExpectationEvaluator);
        
        sessionManager.addModel(complianceRangedModel);
        sessionManager.addModelExpectationEvaluator(complianceRangedModelExpectationEvaluator);

        JCpSimDataGatherer g = new JCpSimDataGatherer(client);
        g.addEventListener(sessionManager);

        g.start();
        Thread.sleep(60000);
        g.stop();
        
    }
}
