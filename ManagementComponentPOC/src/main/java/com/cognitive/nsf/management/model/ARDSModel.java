/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author esteban
 */
public class ARDSModel extends AbstractModel {

    public static String NAME = "ARDS"; 
    
    public ARDSModel() {
        super(NAME, 10);
    }

    public void setGlobals(StatefulKnowledgeSession ksession) {
    }

    public void insertInitialFacts(StatefulKnowledgeSession ksession) {
    }

    @Override
    public String toString() {
        return "ARDSModel{" + '}';
    }
    
    
}
