/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.data;

import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ValueModifiedToken  implements InternalToken{
    private final JCpSimParameter target;
    private final Double oldValue;
    private final Double newValue;
    private final String source;

    public ValueModifiedToken(JCpSimParameter target, Double oldValue, Double newValue, String source) {
        this.target = target;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.source = source;
    }

    public JCpSimParameter getTarget() {
        return target;
    }

    public Double getOldValue() {
        return oldValue;
    }

    public Double getNewValue() {
        return newValue;
    }

    public String getSource() {
        return source;
    }
    
    public boolean isAutoRetractable() {
        return true;
    }

    @Override
    public String toString() {
        return "ValueModifiedToken{" + "target=" + target + ", oldValue=" + oldValue + ", newValue=" + newValue + ", source=" + source + '}';
    }
    
}
