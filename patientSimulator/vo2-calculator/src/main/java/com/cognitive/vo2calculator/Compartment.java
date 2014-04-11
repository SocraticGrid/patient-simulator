/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.vo2calculator;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esteban
 */
public class Compartment {
    
    private Map<String,Double> attributes = new HashMap<String, Double>();
    
    public Compartment() {
    }
    
    public void set(String attribute, double value){
        if(attributes.containsKey(attribute)){
            throw new IllegalArgumentException("Attribute '"+attribute+"' already defined!");
        }
        this.attributes.put(attribute, value);
    }
    
    public double getVQ(){
        return this.attributes.get("VQ");
    }
    
    public double getPH(){
        return this.attributes.get("pH");
    }
    
    public double getPO2(){
        return this.attributes.get("PO2");
    }
    
    public double getPCO2(){
        return this.attributes.get("PCO2");
    }
    
    public double getCaO2(){
        return this.attributes.get("CaO2");
    }
    
    public double getCaCO2(){
        return this.attributes.get("CaCO2");
    }
    
    public double getR(){
        return this.attributes.get("R");
    }
    
    public double getVN2(){
        return this.attributes.get("VN2");
    }
    
    public double getQ(){
        return this.attributes.get("Q");
    }
    
    public double getAaDO2(){
        return this.attributes.get("AaDO2");
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getCaCO2()) ^ (Double.doubleToLongBits(this.getCaCO2()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getCaO2()) ^ (Double.doubleToLongBits(this.getCaO2()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getPCO2()) ^ (Double.doubleToLongBits(this.getPCO2()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getPH()) ^ (Double.doubleToLongBits(this.getPH()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getPO2()) ^ (Double.doubleToLongBits(this.getPO2()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getQ()) ^ (Double.doubleToLongBits(this.getQ()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getR()) ^ (Double.doubleToLongBits(this.getR()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getVN2()) ^ (Double.doubleToLongBits(this.getVN2()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getVQ()) ^ (Double.doubleToLongBits(this.getVQ()) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.getAaDO2()) ^ (Double.doubleToLongBits(this.getAaDO2()) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Compartment other = (Compartment) obj;
 
        if (this.getVQ() != other.getVQ()) {
            return false;
        }
        if (this.getCaCO2() != other.getCaCO2()) {
            return false;
        }
        if (this.getPCO2() != other.getPCO2()) {
            return false;
        }
        if (this.getCaO2() != other.getCaO2()) {
            return false;
        }
        if (this.getPH() != other.getPH()) {
            return false;
        }
        if (this.getPO2() != other.getPO2()) {
            return false;
        }
        if (this.getQ() != other.getQ()) {
            return false;
        }
        if (this.getR() != other.getR()) {
            return false;
        }
        if (this.getVN2() != other.getVN2()) {
            return false;
        }
        if (this.getAaDO2() != other.getAaDO2()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder("Compartment{");
        for (Map.Entry<String, Double> entry : attributes.entrySet()) {
            sb.append(" '");
            sb.append(entry.getKey());
            sb.append("': '");
            sb.append(entry.getValue());
            sb.append("',");
        }
        sb.append("}");
        
        return "Compartment{" + "attributes=" + attributes + '}';
        
    }

    
    
    
}
