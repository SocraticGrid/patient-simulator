/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.data;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author esteban
 */
public class JCpSimDataImpl implements JCpSimData {

    protected long time;
    protected Map<JCpSimParameter, Double> parameters = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
    
    public void setTime(long value){
        parameters.put(JCpSimParameter.TIME, (double)value);
        this.time = value;
    } 
    
    public long getTime() {
        return this.time;
    }

    public void set(JCpSimParameter paramter, Double value){
        this.parameters.put(paramter, value);
    }
    
    public double get(JCpSimParameter parameter) {
        if (parameter == JCpSimParameter.TIME){
            return this.time;
        }
        
        Double result = this.parameters.get(parameter); 
        return result==null?0:result;
    }

    public Map<JCpSimParameter, Double> getData() {
        return parameters;
    }
    
    public boolean hasEquivalentInputParameters(JCpSimData data){
        
        for (Map.Entry<JCpSimParameter, Double> entry : this.parameters.entrySet()) {
            if (entry.getKey().name().startsWith("V_") || entry.getKey().name().startsWith("P_")){
                if (Double.compare(entry.getValue(), data.get(entry.getKey())) != 0){
                    System.out.println(entry.getKey() +" is different: "+entry.getValue() +" != "+data.get(entry.getKey()));
                   return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean hasEquivalentGasOutputParameters(JCpSimData data){
        
        for (Map.Entry<JCpSimParameter, Double> entry : this.parameters.entrySet()) {
            switch(entry.getKey()){
                case O_PCO2:
                case O_PO2:
                case O_PH:
                case O_R:
                    if(Double.compare(entry.getValue(), data.get(entry.getKey())) != 0){
                        return false;
                    }
                    break;
            }
        }
        
        return true;
    }
}
