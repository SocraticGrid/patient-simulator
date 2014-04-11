/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esteban
 */
public class Delta {
    private final String parameter;

    private final Map<String,Double> values = new HashMap<String, Double>();
    
    public Delta(String parameter) {
        this.parameter = parameter;
    }
    
    public synchronized void setValue(String source, double value){
        this.values.put(source, value);
    }
    
    public synchronized void addValue(String source, double value){
        Double previousValue = this.values.get(source);
        
        if (previousValue != null){
            value += previousValue;
        }
        
        this.setValue(source, value);
    }
    
    public double getValue(String source){
        return this.getValue(source);
    }
    
    public synchronized double getTotal(){
        double result = 0;
        
        for (Double value : this.values.values()) {
            result += value;
        }
        
        return result;
    }

    public String getParameter() {
        return parameter;
    }
    
}
