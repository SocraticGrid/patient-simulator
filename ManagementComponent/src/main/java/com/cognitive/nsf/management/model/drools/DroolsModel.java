/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.drools;

import com.cognitive.nsf.management.model.Model;
import com.cognitive.nsf.management.model.ModelSessionManager;
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

/**
 * Drools' based model. This class holds an internal Drools session that is
 * notified each time a new data set from JCpSim is available.
 *
 * @author esteban
 */
public class DroolsModel implements Model {

    public static class Factory {

        private final Map<Resource, ResourceType> resources = new LinkedHashMap<Resource, ResourceType>();

        public Factory() {
            this.resources.put(ResourceFactory.newClassPathResource("com/cognitive/nsf/management/model/drools/DroolsModelRules.drl"), ResourceType.DRL);
        }

        public Factory addResource(Resource resource, ResourceType type) {
            this.resources.put(resource, type);
            return this;
        }

        public DroolsModel createDroolsModelInstance() {
            KnowledgeBase kbase = this.createKBase();
            DroolsModel model = new DroolsModel(kbase);
            
            return model;
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
    private ModelSessionManager manager;
    
    private DroolsModel(KnowledgeBase kbase) {
        this.kbase = kbase;
        this.ksession = this.kbase.newStatefulKnowledgeSession();
    }
    
    public void init(ModelSessionManager manager) {
        this.manager = manager;
        this.ksession.setGlobal("model", this);
        this.ksession.setGlobal("manager", manager);
    }

    public void processData(JCpSimData data) {
        this.ksession.insert(data);
        this.ksession.fireAllRules();
    }

    public void dispose(){
        ksession.dispose();
    }

    @Override
    public String toString() {
        return "DroolsModel{" + '}';
    }
    
}
