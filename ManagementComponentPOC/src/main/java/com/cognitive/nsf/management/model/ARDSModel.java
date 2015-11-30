/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import org.kie.api.runtime.KieSession;


/**
 *
 * @author esteban
 */
public class ARDSModel extends AbstractModel {

    public static String NAME = "ARDS"; 
    
    public ARDSModel() {
        super(NAME, 10);
    }

    public void setGlobals(KieSession ksession) {
    }

    public void insertInitialFacts(KieSession ksession) {
    }

    @Override
    public String toString() {
        return "ARDSModel{" + '}';
    }
    
    
}
