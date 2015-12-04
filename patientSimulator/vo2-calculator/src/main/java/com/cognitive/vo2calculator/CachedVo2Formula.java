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
package com.cognitive.vo2calculator;

import com.cognitive.vo2calculator.cache.LRUCache;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author esteban
 */
public class CachedVo2Formula extends Vo2Formula{

    private LRUCache<String, List<Compartment>> cache;

    public CachedVo2Formula(int cacheSize) {
        this.cache = new LRUCache<String, List<Compartment>>(cacheSize);
    }
    
    
    
    @Override
    public synchronized List<Compartment> compute() throws Exception {

        String key = createCacheKey();
        
        List<Compartment> result = cache.get(key);
        
        if (result == null){
            result = super.compute();
            cache.put(key, result);
        }

        return result;
    }
    
    private String createCacheKey(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.getVo2());
        builder.append("#");
        builder.append(this.getVco2());
        builder.append("#");
        builder.append(this.getCo());
        builder.append("#");
        builder.append(this.getHgb());
        builder.append("#");
        builder.append(this.getTemp());
        builder.append("#");
        builder.append(this.getPb());
        builder.append("#");
        builder.append(this.getFiO2());
        builder.append("#");
        
        for (double d : this.getVqs()) {
            builder.append(d);
            builder.append("#");
        }
        
        for (String s : this.getVqsIndex()) {
            builder.append(s);
            builder.append("#");
        }
        
        return builder.toString();
    }
    
}
