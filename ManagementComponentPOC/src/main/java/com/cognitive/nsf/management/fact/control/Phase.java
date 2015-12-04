/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
