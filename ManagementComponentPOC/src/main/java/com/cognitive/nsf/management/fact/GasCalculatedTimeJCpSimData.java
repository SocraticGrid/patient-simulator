/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.fact;

import java.util.EnumMap;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

/**
 * Data containing all the parameter values at the time that the blood gas is calculated.
 * (Data when a gas is calculated no matter if there is a change or not in the 
 * resulting gas)
 * @author esteban
 */
public class GasCalculatedTimeJCpSimData {
    private Map<JCpSimParameter, Double> data = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
    private final long timestamp;

    
    public GasCalculatedTimeJCpSimData(Map<JCpSimParameter, Double> data) {
        this(data, System.currentTimeMillis());
    }
    
    public GasCalculatedTimeJCpSimData(Map<JCpSimParameter, Double> data, long timestamp) {
        this.data.putAll(data);
        this.timestamp = timestamp;
    }
    
    public Map<JCpSimParameter, Double> getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
}
