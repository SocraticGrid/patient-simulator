/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ManagementConsoleEventListener implements AgendaEventListener, WorkingMemoryEventListener{

    private final ManagementConsole managementConsole;

    private Map<Class, Long> factCounter = new HashMap<Class, Long>(); 
    
    
    private List<String> hiddenFacts = new ArrayList<String>() {
        {
            add("org.jcpsim.data.JCpSimDataImpl");
            //    add("com.cognitive.nsf.management.GasCalculatedTimeJCpSimData");
        }
    };
    
    private Class[] topNFacts = new Class[5]; 
    
    private Comparator<Class> topNComparator = new Comparator<Class>() {

        public int compare(Class o1, Class o2) {
            Long val1 = factCounter.get(o1);
            Long val2 = factCounter.get(o2);

            if (val1 == null){
                val1 = 0L;
            }
            if (val2 == null){
                val2 = 0L;
            }
            
            if (o1 != null && o2 != null && val1 == val2){
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
            return val2.compareTo(val1);
        }
    };
    
    public ManagementConsoleEventListener(ManagementConsole managementConsole) {
        this.managementConsole = managementConsole;
        
        
        
    }
    
    
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
        
        Long count = this.factCounter.get(event.getObject().getClass());
        
        if (count == null){
            count = 0L;
        }
        
        this.factCounter.put(event.getObject().getClass(), ++count);
        
        this.updateTopN(event.getObject().getClass());
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
        
        Long count = this.factCounter.get(event.getOldObject().getClass());
        
        //Should never go through here
        if (count == null){
            count = 0L;
        }
        
        this.factCounter.put(event.getOldObject().getClass(), --count);
        this.updateTopN(event.getOldObject().getClass());
    }
    
    public synchronized void updateTopN(Class clazz){

        boolean alreadyInTopN = false;
        for (int i = 0; i < topNFacts.length; i++) {
            if (clazz == topNFacts[i]){
                alreadyInTopN = true;
                break;
            }
            
        }
        
        Class[] factsToBeSorted;
        if (alreadyInTopN){
            factsToBeSorted = new Class[topNFacts.length];
        } else{
            factsToBeSorted = new Class[topNFacts.length+1]; 
            factsToBeSorted[topNFacts.length] = clazz;
        }
        
         
        System.arraycopy(topNFacts, 0, factsToBeSorted, 0, this.topNFacts.length);
        
        
        
        Arrays.sort(factsToBeSorted, topNComparator);
        
        System.arraycopy(factsToBeSorted, 0, this.topNFacts, 0, this.topNFacts.length);

        this.managementConsole.logFactCount(topNFacts, factCounter);
    }
    
}
