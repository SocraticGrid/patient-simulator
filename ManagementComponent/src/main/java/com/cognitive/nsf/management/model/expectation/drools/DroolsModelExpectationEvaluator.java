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
package com.cognitive.nsf.management.model.expectation.drools;

import com.cognitive.nsf.management.model.Model;
import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluatorImpl;
import com.cognitive.nsf.management.model.expectation.PolicyEnforcer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jcpsim.data.JCpSimData;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;


public class DroolsModelExpectationEvaluator extends ModelExpectationEvaluatorImpl {

    public static class Factory {

        private final Map<Resource, ResourceType> resources = new LinkedHashMap<Resource, ResourceType>();
        private final Model model;
        public Factory(Model model) {
            this.model = model;
            this.resources.put(ResourceFactory.newClassPathResource("com/cognitive/nsf/management/model/drools/DroolsModelExpectationEvaluatorRules.drl"), ResourceType.DRL);
        }

        public Factory addResource(Resource resource, ResourceType type) {
            this.resources.put(resource, type);
            return this;
        }

        public DroolsModelExpectationEvaluator createDroolsModelExpectationEvaluatorInstance() {
            KieBase kbase = this.createKBase();
            DroolsModelExpectationEvaluator expectationEvaluator = new DroolsModelExpectationEvaluator(model, kbase);
            
            return expectationEvaluator;
        }

        private KieBase createKBase() {

            KieHelper kieHelper = new KieHelper();
            
            for (Map.Entry<Resource, ResourceType> entry : resources.entrySet()) {
                kieHelper.addResource(entry.getKey(), entry.getValue());
            }
             
            Results results = kieHelper.verify();
            if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
                List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
                for (Message message : messages) {
                    System.out.printf("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
                }

                throw new IllegalStateException("Compilation Errors");
            }

            KieBaseConfiguration conf = KieServices.Factory.get().newKieBaseConfiguration();
            conf.setOption(EventProcessingOption.STREAM);
            return kieHelper.build(conf);
        }
    }
    
    private final KieBase kbase;
    private KieSession ksession;
    
    private DroolsModelExpectationEvaluator(Model model, KieBase kbase) {
        super(model);
        this.kbase = kbase;
        this.ksession = this.kbase.newKieSession();
    }

    @Override
    public void configure(PolicyEnforcer policyEnforcer) {
        super.configure(policyEnforcer);
        
        this.ksession.setGlobal("model", this.getModel());
        this.ksession.setGlobal("policyEnforcer", this.getPolicyEnforcer());
    }
    

    @Override
    public void processData(JCpSimData data) {
        this.ksession.insert(data);
        this.ksession.fireAllRules();
    }
    
}
