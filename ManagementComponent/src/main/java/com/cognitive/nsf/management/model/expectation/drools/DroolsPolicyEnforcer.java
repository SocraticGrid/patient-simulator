/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.expectation.drools;

import com.cognitive.nsf.management.model.ModelSessionManager;
import com.cognitive.nsf.management.model.drools.DroolsModel;
import com.cognitive.nsf.management.model.expectation.ExpectationResults;
import com.cognitive.nsf.management.model.expectation.PolicyEnforcer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;


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
            KnowledgeBase kbase = this.createKBase();
            DroolsPolicyEnforcer policyEnforcer = new DroolsPolicyEnforcer(kbase);
            
            return policyEnforcer;
        }

        private KnowledgeBase createKBase() {

            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            for (Map.Entry<Resource, ResourceType> entry : resources.entrySet()) {
                kbuilder.add(entry.getKey(), entry.getValue());
                if (kbuilder.hasErrors()) {
                    Logger.getLogger(DroolsModel.class.getName()).log(Level.SEVERE, "Compilation Errors in {0}", entry.getKey());
                    Iterator<KnowledgeBuilderError> iterator = kbuilder.getErrors().iterator();
                    while (iterator.hasNext()) {
                        KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                        Logger.getLogger(DroolsModel.class.getName()).log(Level.SEVERE, knowledgeBuilderError.getMessage());
                        System.out.println(knowledgeBuilderError.getMessage());
                    }
                    throw new IllegalStateException("Compilation Errors");
                }
            }

            KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            conf.setOption( EventProcessingOption.STREAM );
            
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

            return kbase;
        }
    }
    
    private final KnowledgeBase kbase;
    private StatefulKnowledgeSession ksession;

    private DroolsPolicyEnforcer(KnowledgeBase kbase) {
        this.kbase = kbase;
        this.ksession = this.kbase.newStatefulKnowledgeSession();
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
