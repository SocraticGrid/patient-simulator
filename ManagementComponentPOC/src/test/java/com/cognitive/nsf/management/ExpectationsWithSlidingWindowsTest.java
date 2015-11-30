/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.fact.ConstraintViolation;
import com.cognitive.nsf.management.fact.GasCalculatedTimeJCpSimData;
import com.cognitive.nsf.management.fact.control.Phase;
import com.cognitive.nsf.management.rule.BestModelAccumulateFunction;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.time.SessionPseudoClock;
import org.jcpsim.data.JCpSimParameter;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.AccumulateFunctionOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 *
 * @author esteban
 */
public class ExpectationsWithSlidingWindowsTest {

    KieSession ksession;
    SessionPseudoClock clock;
    
    @Test
    public void doTest() throws InterruptedException {

        Map<Resource, ResourceType> resources = new LinkedHashMap<Resource, ResourceType>();
        //resources.put(ResourceFactory.newClassPathResource("rules/slidingWindowsTest.drl"), ResourceType.DRL);
        resources.put(ResourceFactory.newClassPathResource("manager/rules/modelManagement.drl"), ResourceType.DRL);
        resources.put(ResourceFactory.newClassPathResource("manager/rules/lifecycleManagement.drl"), ResourceType.DRL);

        KieBase kbase = this.createKbase(resources);

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

    private KieSession createKsession(KieBase kbase) {
        KieSessionConfiguration config = KieServices.Factory.get().newKieSessionConfiguration();;
        config.setOption(ClockTypeOption.get("pseudo"));

        KieSession ksession = kbase.newKieSession(config, null);

        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                System.out.print("Activation Created: " + event.getMatch().getRule().getName() + ": [");
                for (FactHandle factHandle : event.getMatch().getFactHandles()) {
                    System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
                }
                System.out.println("]");

                if (event.getMatch().getRule().getName().equals("Increasing PIP will decrease PaCO2 when PIP is [12,50]")) {
                    List<? extends FactHandle> factHandles = event.getMatch().getFactHandles();
                    for (FactHandle factHandle : factHandles) {
                        if (factHandle instanceof EventFactHandle) {
                            EventFactHandle e = (EventFactHandle) factHandle;
                            System.out.println("\t" + e.getObject() + "[" + e.getId() + "] inserted at " + e.getStartTimestamp());
                        }
                    }

                }
            }

            @Override
            public void matchCancelled(MatchCancelledEvent event) {
                System.out.println("Activation Cancelled: " + event.getMatch().getRule().getName());
            }

            @Override
            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                System.out.println("Before Activation Fired: " + event.getMatch().getRule().getName());
            }

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                System.out.println("After Activation Fired: " + event.getMatch().getRule().getName());
            }
        });

        ksession.addEventListener(new DefaultRuleRuntimeEventListener() {
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
            public synchronized void objectDeleted(ObjectDeletedEvent event) {
                String className = event.getOldObject().getClass().getName();
                System.out.println(className + " retracted: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
            }
        });

        return ksession;
    }

    private KieBase createKbase(Map<Resource, ResourceType> resources) {
        
        //"drools.accumulate.function.bestModel"
        KieHelper helper = new KieHelper(AccumulateFunctionOption.get("bestModel", new BestModelAccumulateFunction()));
        
        for (Map.Entry<Resource, ResourceType> entry : resources.entrySet()) {
            helper.addResource(entry.getKey(), entry.getValue());
        }
        
        Results results = helper.verify();
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                String outMessage = String.format("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
                System.out.printf(outMessage);
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, outMessage);
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        
        KieBaseConfiguration conf = KieServices.Factory.get().newKieBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);
        
        return helper.build();
    }
}
