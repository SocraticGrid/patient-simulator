/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.fact.control;

import java.util.Date;

/**
 *
 * @author esteban
 */
public class Phase {
    
    public static enum PhaseName{
        UNDEFINED("Undefined"), //When no good ventilator model is found
        NORMAL("Normal"), //The vent. model is running. Threashold is monitored.
        STABILIZATION("Stabilization"), //The disease is stopped and some Blood Gas cylces are executed.
        CALCULATE_VIOLATIONS("Calculating Constraint Violations"), //After a period of stabilization, the Constraint Violations for the last X Blood Gases is calculated
        EXTENDED_STABILIZATION("Extended Stabilization"); 
        
        private String friendlyName;

        public String getFriendlyName() {
            return friendlyName;
        }

        private PhaseName(String friendlyName) {
            this.friendlyName = friendlyName;
        }
        
    }
    
    private final PhaseName name;
    private Date activationDate;

    public Phase(PhaseName name, Date activationDate) {
        this.name = name;
        this.activationDate = activationDate;
    }
    
    public Phase(PhaseName name, long activationTimestamp) {
        this(name, new Date(activationTimestamp));
    }

    public PhaseName getName() {
        return name;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }
    
}
