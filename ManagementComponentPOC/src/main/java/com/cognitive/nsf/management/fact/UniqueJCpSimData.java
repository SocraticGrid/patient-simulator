/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.fact;

import java.util.EnumMap;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

/**
 * Data that represents when a variation in one or more V_ and P_ parameters is 
 * found. This doesn't mean that the Blood Gas has been already calculated.
 * @author esteban
 */
public class UniqueJCpSimData {
    private Map<JCpSimParameter, Double> data = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);

    public UniqueJCpSimData(Map<JCpSimParameter, Double> data) {
        this.data.putAll(data);
    }
    
    public Map<JCpSimParameter, Double> getData() {
        return data;
    }
    
}
