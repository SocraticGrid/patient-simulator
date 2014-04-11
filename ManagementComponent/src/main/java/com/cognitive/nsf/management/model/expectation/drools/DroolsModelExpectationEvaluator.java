/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.expectation.drools;

import com.cognitive.nsf.management.model.Model;
import com.cognitive.nsf.management.model.drools.DroolsModel;
import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluatorImpl;
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
import org.jcpsim.data.JCpSimData;


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
            KnowledgeBase kbase = this.createKBase();
            DroolsModelExpectationEvaluator expectationEvaluator = new DroolsModelExpectationEvaluator(model, kbase);
            
            return expectationEvaluator;
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
    
    private DroolsModelExpectationEvaluator(Model model, KnowledgeBase kbase) {
        super(model);
        this.kbase = kbase;
        this.ksession = this.kbase.newStatefulKnowledgeSession();
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
