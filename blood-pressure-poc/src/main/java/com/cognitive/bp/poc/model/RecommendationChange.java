/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitive.bp.poc.model;

/**
 *
 * @author esteban
 */
public class RecommendationChange {
    public static enum PARAMETER{
        AA_DELAY;
    }
    
    public final PARAMETER parameter; 
    public final String newValue; 

    public RecommendationChange(PARAMETER parameter, String newValue) {
        this.parameter = parameter;
        this.newValue = newValue;
    }

    public PARAMETER getParameter() {
        return parameter;
    }

    public String getNewValue() {
        return newValue;
    }
    
    public long getNewValueAsLong(){
        return Long.valueOf(newValue);
    }
    
    
    
}
