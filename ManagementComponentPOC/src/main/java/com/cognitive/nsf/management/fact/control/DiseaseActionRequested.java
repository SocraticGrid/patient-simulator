/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.fact.control;

/**
 *
 * @author esteban
 */
public class DiseaseActionRequested {
    
    public static enum Action{
        PAUSE_DISEASE,
        RESUME_DISEASE;
    }
    
    private final Action action;

    public DiseaseActionRequested(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
    
}
