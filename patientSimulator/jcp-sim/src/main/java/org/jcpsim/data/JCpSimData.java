/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.data;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author esteban
 */
public interface JCpSimData extends Serializable{
    
    public void setTime(long value);
    public long getTime();
    public double get(JCpSimParameter parameter);
    public void set(JCpSimParameter paramter, Double value);
    public Map<JCpSimParameter, Double> getData();
    public boolean hasEquivalentInputParameters(JCpSimData data);
    public boolean hasEquivalentGasOutputParameters(JCpSimData data);
}

