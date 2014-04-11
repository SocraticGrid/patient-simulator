/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ManualRecommendation {
    private JCpSimParameter parameter;
    private double value;

    public ManualRecommendation(JCpSimParameter parameter, double value) {
        this.parameter = parameter;
        this.value = value;
    }

    public JCpSimParameter getParameter() {
        return parameter;
    }

    public void setParameter(JCpSimParameter parameter) {
        this.parameter = parameter;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
}
