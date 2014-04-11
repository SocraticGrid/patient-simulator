/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive;

import java.util.ArrayList;
import java.util.List;
import org.drools.common.DefaultFactHandle;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.rule.FactHandle;

/**
 *
 * @author esteban
 */
public class DroolsEventListener implements AgendaEventListener, WorkingMemoryEventListener{

    private List<String> hiddenFacts = new ArrayList<String>() {
        {
            add("org.jcpsim.data.JCpSimDataImpl");
            //    add("com.cognitive.nsf.management.GasCalculatedTimeJCpSimData");
        }
    };
    
    public void activationCreated(ActivationCreatedEvent event) {
        System.out.print("Activation Created: " + event.getActivation().getRule().getName() + ": [");
        for (FactHandle factHandle : event.getActivation().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");

    }

    public void activationCancelled(ActivationCancelledEvent event) {
        System.out.print("Activation Cancelled: " + event.getActivation().getRule().getName()+ ":[");
        for (FactHandle factHandle : event.getActivation().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        System.out.print("Before Activation Fired: " + event.getActivation().getRule().getName()+ ":[");
        for (FactHandle factHandle : event.getActivation().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");
    }

    public void afterActivationFired(AfterActivationFiredEvent event) {
        System.out.print("After Activation Fired: " + event.getActivation().getRule().getName()+ ":[");
        for (FactHandle factHandle : event.getActivation().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    public synchronized void objectInserted(ObjectInsertedEvent event) {
        String className = event.getObject().getClass().getName();
        if (!hiddenFacts.contains(className)) {
            System.out.println(className + " inserted: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
        }
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        String className = event.getObject().getClass().getName();
        if (!hiddenFacts.contains(className)) {
            System.out.println(className + " updated: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
        }
    }

    public synchronized void objectRetracted(ObjectRetractedEvent event) {
        String className = event.getOldObject().getClass().getName();
        if (!hiddenFacts.contains(className)) {
            System.out.println(className + " retracted: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
        }
    }
    
    
}
