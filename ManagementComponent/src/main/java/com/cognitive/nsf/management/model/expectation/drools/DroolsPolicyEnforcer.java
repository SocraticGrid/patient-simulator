/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.expectation.drools;

import com.cognitive.nsf.management.model.ModelSessionManager;
import com.cognitive.nsf.management.model.expectation.ExpectationResults;
import com.cognitive.nsf.management.model.expectation.PolicyEnforcer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;


public class DroolsPolicyEnforcer extends PolicyEnforcer {

    
    public static class Factory {

        private final Map<Resource, ResourceType> resources = new LinkedHashMap<Resource, ResourceType>();
       
        public Factory() {
            this.resources.put(ResourceFactory.newClassPathResource("com/cognitive/nsf/management/model/drools/DroolsPolicyEnforcerRules.drl"), ResourceType.DRL);
        }

        public Factory addResource(Resource resource, ResourceType type) {
            this.resources.put(resource, type);
            return this;
        }

        public DroolsPolicyEnforcer createDroolsPolicyEnforcerInstance() {
            KieBase kbase = this.createKBase();
            DroolsPolicyEnforcer policyEnforcer = new DroolsPolicyEnforcer(kbase);
            
            return policyEnforcer;
        }

        private KieBase createKBase() {

            KieHelper kieHelper = new KieHelper();
            
            for (Map.Entry<org.kie.api.io.Resource, org.kie.api.io.ResourceType> entry : resources.entrySet()) {
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
            conf.setOption(org.kie.api.conf.EventProcessingOption.STREAM);
            return kieHelper.build(conf);
        }
    }
    
    private final KieBase kbase;
    private KieSession ksession;

    private DroolsPolicyEnforcer(KieBase kbase) {
        this.kbase = kbase;
        this.ksession = this.kbase.newKieSession();
    }

    @Override
    public void configure(ModelSessionManager modelSessionManager) {
        super.configure(modelSessionManager);
        
        this.ksession.setGlobal("manager", modelSessionManager);
    }
    
    
    
    @Override
    public void onExpectationResults(ExpectationResults results) {
        this.ksession.insert(results);
        this.ksession.fireAllRules();
    }
    
}
