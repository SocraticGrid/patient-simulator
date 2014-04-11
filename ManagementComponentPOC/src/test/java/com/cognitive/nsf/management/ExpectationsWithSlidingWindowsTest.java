/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.fact.ConstraintViolation;
import com.cognitive.nsf.management.fact.GasCalculatedTimeJCpSimData;
import com.cognitive.nsf.management.fact.control.Phase;
import com.cognitive.nsf.management.model.ARDSModel;
import com.cognitive.nsf.management.model.DiseaseModel;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.event.rule.DefaultWorkingMemoryEventListener;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;
import org.drools.time.SessionPseudoClock;
import org.jcpsim.data.JCpSimParameter;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class ExpectationsWithSlidingWindowsTest {

    StatefulKnowledgeSession ksession;
    SessionPseudoClock clock;
    
    @Test
    public void doTest() throws InterruptedException {

        Map<Resource, ResourceType> resources = new LinkedHashMap<Resource, ResourceType>();
        //resources.put(ResourceFactory.newClassPathResource("rules/slidingWindowsTest.drl"), ResourceType.DRL);
        resources.put(ResourceFactory.newClassPathResource("manager/rules/modelManagement.drl"), ResourceType.DRL);
        resources.put(ResourceFactory.newClassPathResource("manager/rules/lifecycleManagement.drl"), ResourceType.DRL);

        KnowledgeBase kbase = this.createKbase(this.compileResources(resources));

        ksession = this.createKsession(kbase);
        clock = ksession.getSessionClock();

        
        ksession.setGlobal("modelThreshold", 0.0);
        
        Phase phase = new Phase(Phase.PhaseName.NORMAL, new Date(clock.getCurrentTime()));
        ksession.insert(phase);
        
        
        insertCV(1);
        insertBG();
        insertCV(20);
        insertCV(300);
        insertBG();
        insertCV(4000);
        insertBG();
        insertBG();
        insertCV(50000);
        insertBG();
        insertCV(600000);
        insertBG();
        insertBG();
        
        
        Thread.sleep(5000);

    }
    
    private void insertCV(double weight){
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.insert(ConstraintViolation.newSoftConstraintViolation(JCpSimParameter.V_PEEP, 0.0, weight, null, null));
        ksession.fireAllRules();
    }
    
    private void insertBG(){
        Map<JCpSimParameter, Double> values = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
        
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.insert(new GasCalculatedTimeJCpSimData(values, clock.getCurrentTime()));
        ksession.fireAllRules();
    }

    private StatefulKnowledgeSession createKsession(KnowledgeBase kbase) {
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        config.setOption(ClockTypeOption.get("pseudo"));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(config, null);

        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void activationCreated(ActivationCreatedEvent event) {
                System.out.print("Activation Created: " + event.getActivation().getRule().getName() + ": [");
                for (FactHandle factHandle : event.getActivation().getFactHandles()) {
                    System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
                }
                System.out.println("]");

                if (event.getActivation().getRule().getName().equals("Increasing PIP will decrease PaCO2 when PIP is [12,50]")) {
                    List<? extends FactHandle> factHandles = event.getActivation().getFactHandles();
                    for (FactHandle factHandle : factHandles) {
                        if (factHandle instanceof EventFactHandle) {
                            EventFactHandle e = (EventFactHandle) factHandle;
                            System.out.println("\t" + e.getObject() + "[" + e.getId() + "] inserted at " + e.getStartTimestamp());
                        }
                    }

                }
            }

            @Override
            public void activationCancelled(ActivationCancelledEvent event) {
                System.out.println("Activation Cancelled: " + event.getActivation().getRule().getName());
            }

            @Override
            public void beforeActivationFired(BeforeActivationFiredEvent event) {
                System.out.println("Before Activation Fired: " + event.getActivation().getRule().getName());
            }

            @Override
            public void afterActivationFired(AfterActivationFiredEvent event) {
                System.out.println("After Activation Fired: " + event.getActivation().getRule().getName());
            }
        });

        ksession.addEventListener(new DefaultWorkingMemoryEventListener() {
            @Override
            public synchronized void objectInserted(ObjectInsertedEvent event) {
                String className = event.getObject().getClass().getName();
                System.out.println(className + " inserted: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                String className = event.getObject().getClass().getName();
                System.out.println(className + " updated: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
            }

            @Override
            public synchronized void objectRetracted(ObjectRetractedEvent event) {
                String className = event.getOldObject().getClass().getName();
                System.out.println(className + " retracted: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
            }
        });

        return ksession;
    }

    private KnowledgeBase createKbase(Collection<KnowledgePackage> knowledgePackages) {
        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        kbase.addKnowledgePackages(knowledgePackages);

        return kbase;
    }

    private Collection<KnowledgePackage> compileResources(Map<Resource, ResourceType> resources) {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        conf.setProperty("drools.accumulate.function.bestModel", "com.cognitive.nsf.management.rule.BestModelAccumulateFunction");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);

        for (Map.Entry<Resource, ResourceType> entry : resources.entrySet()) {
            kbuilder.add(entry.getKey(), entry.getValue());
            if (kbuilder.hasErrors()) {
                Logger.getLogger(ExpectationsUsingMockModelsAndRecommendationsTest.class.getName()).log(Level.SEVERE, "Compilation Errors in {0}", entry.getKey());
                Iterator<KnowledgeBuilderError> iterator = kbuilder.getErrors().iterator();
                while (iterator.hasNext()) {
                    KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                    Logger.getLogger(ExpectationsUsingMockModelsAndRecommendationsTest.class.getName()).log(Level.SEVERE, knowledgeBuilderError.getMessage());
                    System.out.println(knowledgeBuilderError.getMessage());
                }
                throw new IllegalStateException("Compilation Errors");
            }
        }

        return kbuilder.getKnowledgePackages();
    }
}