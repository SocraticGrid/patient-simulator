/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognitive.nsf.management.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ConstraintViolationWeightIndicator {
    
    private class Bucket{
        private double min;
        private double max;
        private double weight;

        public Bucket(double min, double max, double weight) {
            this.min = min;
            this.max = max;
            this.weight = weight;
        }

        private boolean contains(double value) {
            return value > min && value <= max;
        }
        
        
    }
    
    private String name;
    private Map<JCpSimParameter, List<Bucket>> buckets = new EnumMap<JCpSimParameter, List<Bucket>>(JCpSimParameter.class);

    public ConstraintViolationWeightIndicator(String name) {
        this.name = name;
    }
    
    
    public void addBucket(JCpSimParameter parameter, double min, double max, double weight){
        
        List<Bucket> buckets = this.buckets.get(parameter);
        if (buckets == null){
            buckets = new ArrayList<Bucket>();
            this.buckets.put(parameter, buckets);
        }
        
        buckets.add(new Bucket(min, max, weight));
    }
    
    public double getWeight(JCpSimParameter parameter, double value){
        List<Bucket> buckets = this.buckets.get(parameter);
        
        if (buckets == null){
            throw new IllegalArgumentException("There is no weight configured for parameter "+parameter);
        }
     
        for (Bucket bucket : buckets) {
            if (bucket.contains(value)){
                return bucket.weight;
            }
        }
        
        return -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
