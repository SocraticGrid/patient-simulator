/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.model;

import java.util.EnumMap;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

public class PeakJCpSimData {
    private final Map<JCpSimParameter, Double> data = new EnumMap<>(JCpSimParameter.class);
    private final long timestamp;
    private boolean inBucket;

    public PeakJCpSimData(Map<JCpSimParameter, Double> data) {
        this.data.putAll(data);
        this.timestamp = this.data.get(JCpSimParameter.TIME).longValue();
    }
    
    public Map<JCpSimParameter, Double> getData() {
        return data;
    }

    public boolean isInBucket() {
        return inBucket;
    }

    public void setInBucket(boolean inBucket) {
        this.inBucket = inBucket;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
}
