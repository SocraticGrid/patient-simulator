/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.fact;

import java.util.EnumMap;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

/**
 * Data containing all the parameter values at the time that a change in the blood gas 
 * parameters is detected. (Data when a NEW gas is calculated)
 * @author esteban
 */
public class UniqueGasJCpSimData {
    private Map<JCpSimParameter, Double> data = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);

    public UniqueGasJCpSimData(Map<JCpSimParameter, Double> data) {
        this.data.putAll(data);
    }
    
    public Map<JCpSimParameter, Double> getData() {
        return data;
    }
    
}
