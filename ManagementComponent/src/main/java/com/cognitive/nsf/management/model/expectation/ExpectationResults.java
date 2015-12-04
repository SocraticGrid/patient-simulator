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
package com.cognitive.nsf.management.model.expectation;

import com.cognitive.nsf.management.model.Model;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author esteban
 */
public class ExpectationResults {
    public static class ConstraintViolation{
        public static enum TYPE{
            HARD, SOFT
        }
        
        private final TYPE type;
        private final double weight;

        public ConstraintViolation(TYPE type, double weight) {
            this.type = type;
            this.weight = weight;
        }

        public TYPE getType() {
            return type;
        }

        public double getWeight() {
            return weight;
        }
        
    }
    
    private final Model model;
    private final Set<ExpectationResults.ConstraintViolation> hardViolations = new HashSet<ExpectationResults.ConstraintViolation>();
    private final Set<ExpectationResults.ConstraintViolation> softViolations = new HashSet<ExpectationResults.ConstraintViolation>();
    private double hardTotalWeight;
    private double softTotalWeight;

    public ExpectationResults(Model model) {
        this.model = model;
    }
    
    public void addViolation(ExpectationResults.ConstraintViolation violation){
        switch (violation.getType()){
            case HARD:
                this.hardViolations.add(violation);
                this.hardTotalWeight += violation.getWeight();
                break;
            case SOFT:
                this.softViolations.add(violation);
                this.softTotalWeight += violation.getWeight();
                break;
                default:
                    throw new IllegalArgumentException("Unsupported Constraint Violation type: "+violation.getType());
        }
    }
    
    public void addViolation(ConstraintViolation.TYPE type, double weight){
        this.addViolation(new ConstraintViolation(type, weight));
    }

    public Set<ConstraintViolation> getHardViolations() {
        return Collections.unmodifiableSet(hardViolations);
    }

    public Set<ConstraintViolation> getSoftViolations() {
        return Collections.unmodifiableSet(softViolations);
    }
    
    public Set<ConstraintViolation> getViolations() {
        Set<ConstraintViolation> result = new HashSet<ExpectationResults.ConstraintViolation>();
        
        result.addAll(this.hardViolations);
        result.addAll(this.softViolations);
        
        return result;
    }

    public int getHardCount() {
        return this.hardViolations.size();
    }

    public int getSoftCount() {
        return this.softViolations.size();
    }
    
    public double getHardTotalWeight() {
        return hardTotalWeight;
    }

    public double getSoftTotalWeight() {
        return softTotalWeight;
    }

    public Model getModel() {
        return model;
    }
 
}
