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
package com.cognitive.nsf.management.fact;

import com.cognitive.nsf.management.model.DiseaseModel;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ConstraintViolation {
    
    public static enum TYPE{
        SOFT,
        HARD
    }
    
    private TYPE type;
    private JCpSimParameter parameter;
    private double value;
    private double weight;
    private String source;
    private final DiseaseModel model;

    private ConstraintViolation(TYPE type, JCpSimParameter parameter, double value, double weight, String source, DiseaseModel model) {
        this.type = type;
        this.parameter = parameter;
        this.value = value;
        this.weight = weight;
        this.model = model;
        this.source = source;
    }

    public static ConstraintViolation newSoftConstraintViolation(JCpSimParameter parameter, double value, double weight, String source, DiseaseModel model){
        return new ConstraintViolation(TYPE.SOFT, parameter, value, weight, source, model);
    }
    
    public static ConstraintViolation newHardConstraintViolation(JCpSimParameter parameter, double value, double weight, String source, DiseaseModel model){
        return new ConstraintViolation(TYPE.HARD, parameter, value, weight, source, model);
    }

    public TYPE getType() {
        return type;
    }

    public JCpSimParameter getParameter() {
        return parameter;
    }

    public double getValue() {
        return value;
    }

    public double getWeight() {
        return weight;
    }

    public String getSource() {
        return source;
    }

    public DiseaseModel getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "ConstraintViolation{" + "type='" + type + "', parameter='" + parameter + "', value='" + value + "', weight='" + weight + "', source='" + source+"'}";
    }
    
    
    
}
