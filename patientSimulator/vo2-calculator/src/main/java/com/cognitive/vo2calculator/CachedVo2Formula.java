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
