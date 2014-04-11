/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluator;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class RangedSingleParameterModel implements Model{

    private double delta;
    private final JCpSimParameter parameter;
    private final double min;
    private final double max;
    
    private ModelSessionManager manager;

    public RangedSingleParameterModel(JCpSimParameter parameter, double min, double max, double delta) {
        this.parameter = parameter;
        this.min = min;
        this.max = max;
        this.delta = delta;
    }
    
    public void init(ModelSessionManager manager) {
        this.manager = manager;
    }

    public void processData(JCpSimData data) {
        JCpSimData output = this.clone(data);
        
        double currentValue = data.get(parameter);
        
        if ( Double.compare(currentValue, min) <= 0){
            delta = Math.abs(delta);
        } else if (Double.compare(currentValue, max) >= 0){
            delta = Math.abs(delta)*-1;
        }
        
        output.set(parameter, currentValue + delta);
        
        manager.onResult(this, output);
    }
    
    private JCpSimData clone(JCpSimData original){
        JCpSimData clone = new JCpSimDataImpl();
        for (JCpSimParameter jCpSimParameter : JCpSimParameter.values()) {
            clone.set(jCpSimParameter, original.get(jCpSimParameter));
        }
        return clone;
    }

    @Override
    public String toString() {
        return "RangedSingleParameterModel{" + "delta=" + delta + ", parameter=" + parameter + ", min=" + min + ", max=" + max + '}';
    }
    

}
