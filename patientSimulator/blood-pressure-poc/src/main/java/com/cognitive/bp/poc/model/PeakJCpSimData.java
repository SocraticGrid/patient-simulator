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
