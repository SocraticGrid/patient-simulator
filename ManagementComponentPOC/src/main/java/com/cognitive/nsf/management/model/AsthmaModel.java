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
public class AsthmaModel extends AbstractModel {
    
    public static String NAME = "Asthma"; 

    public AsthmaModel() {
        super(NAME, 1);
    }

    
    
    public void setGlobals(StatefulKnowledgeSession ksession) {
    }

    public void insertInitialFacts(StatefulKnowledgeSession ksession) {
    }

    @Override
    public String toString() {
        return "AsthmaModel{" + '}';
    }
    
}
