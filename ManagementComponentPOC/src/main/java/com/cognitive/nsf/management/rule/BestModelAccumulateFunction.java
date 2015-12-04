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
package com.cognitive.nsf.management.rule;

import com.cognitive.nsf.management.fact.DiseaseModelTotalWeight;
import com.cognitive.nsf.management.model.DiseaseModel;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.kie.api.runtime.rule.AccumulateFunction;

/**
 *
 * @author esteban
 */
public class BestModelAccumulateFunction implements AccumulateFunction{

    private class Context implements Serializable{
        DiseaseModelTotalWeight best;
    }
    
    public Class<?> getResultType() {
        return DiseaseModel.class;
    }

    public Serializable createContext() {
        return new Context();
    }

    public void init(Serializable srlzbl) throws Exception {
    }

    public void accumulate(Serializable srlzbl, Object o) {
        Context context = ((Context)srlzbl);
        DiseaseModelTotalWeight dmtw = ((DiseaseModelTotalWeight)o);
        
        //best is null -> dmtw is the best now
        if (context.best == null){
            context.best = dmtw;
            return;
        }
        
        //best has lower weight -> nothing to do
        if (context.best.getTotalWeight() < dmtw.getTotalWeight()){
            return;
        }
        
        //best has greater weight -> dmtw is the best now
        if (context.best.getTotalWeight() > dmtw.getTotalWeight()){
            context.best = dmtw;
            return;
        }
        
        //both object have the same weight, let's compare the saliences
        if(context.best.getModel().getSalience() < dmtw.getModel().getSalience()){
            context.best = dmtw;
            return;
        }
        
        
    }

    public void reverse(Serializable srlzbl, Object o) throws Exception {
    }

    public Object getResult(Serializable srlzbl) throws Exception {
        return ((Context)srlzbl).best == null? null : ((Context)srlzbl).best.getModel();
    }

    public boolean supportsReverse() {
        return false;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }
    
}
