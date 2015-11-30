/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive;

import java.util.ArrayList;
import java.util.List;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.rule.FactHandle;


/**
 *
 * @author esteban
 */
public class DroolsEventListener implements AgendaEventListener, RuleRuntimeEventListener{

    private List<String> hiddenFacts = new ArrayList<String>() {
        {
            add("org.jcpsim.data.JCpSimDataImpl");
            //    add("com.cognitive.nsf.management.GasCalculatedTimeJCpSimData");
        }
    };
    
    @Override
    public void matchCreated(MatchCreatedEvent event) {
        System.out.print("Activation Created: " + event.getMatch().getRule().getName() + ": [");
        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");
    }
    
    
    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        System.out.print("Activation Cancelled: " + event.getMatch().getRule().getName()+ ":[");
        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        System.out.print("Before Activation Fired: " + event.getMatch().getRule().getName()+ ":[");
        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        System.out.print("After Activation Fired: " + event.getMatch().getRule().getName()+ ":[");
        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.out.print(((DefaultFactHandle) factHandle).getId() + ",");
        }
        System.out.println("]");
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    }

    @Override
    public synchronized void objectInserted(ObjectInsertedEvent event) {
        String className = event.getObject().getClass().getName();
        if (!hiddenFacts.contains(className)) {
            System.out.println(className + " inserted: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
        }
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        String className = event.getObject().getClass().getName();
        if (!hiddenFacts.contains(className)) {
            System.out.println(className + " updated: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
        }
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        String className = event.getOldObject().getClass().getName();
        if (!hiddenFacts.contains(className)) {
            System.out.println(className + " retracted: [" + ((DefaultFactHandle) event.getFactHandle()).getId() + "]");
        }
    }
    
    
}
