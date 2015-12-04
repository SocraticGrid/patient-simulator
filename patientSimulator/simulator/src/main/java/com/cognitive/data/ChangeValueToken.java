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
package com.cognitive.data;

import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;

/**
 * Class that identifies a change that wants to be made on a JCpSimData object.
 * Changes can be relative or absolute. For more information see {@link  #applyChange(org.jcpsim.data.JCpSimData)}. 
 * @author esteban
 */
public class ChangeValueToken  implements InternalToken{
    private final JCpSimParameter target;
    private final String change;
    private final boolean notifyChange;
    private final String source;
    
    //bigger salience -> change applied first
    private final int salience;

    public ChangeValueToken(JCpSimParameter target, String change, String source, boolean notifyChange) {
        this(target, change, source, 0, notifyChange);
    }
    
    public ChangeValueToken(JCpSimParameter target, String change, String source, int salience, boolean notifyChange) {
        this.target = target;
        this.change = change;
        this.notifyChange = notifyChange;
        this.source = source;
        this.salience = salience;
    }

    public ChangeValueToken(JCpSimParameter target, String change, String source) {
        this(target, change, source, true);
    }
    
    public ChangeValueToken(JCpSimParameter target, String change, String source, int salience) {
        this(target, change, source, salience, true);
    }

    /**
     * Applies the Data Modification this object represents to a JCpSimData sample.
     * If the change starts with the letter 'f' (i.e. "f1" or "f-2") the change
     * is absolute and the target parameter of the sample data will be set to
     * it.
     * If the change doesn't start with the letter 'f', the change is relative
     * of the current value the target parameter has in the sample data.
     * For example, a change of "1" means "add 1 to the current value of the target
     * parameter in the sample data". Negative changes are also supported.
     * @param originalData 
     */
    public void applyChange(JCpSimData originalData){
        
        if (this.change.startsWith("f")){
            originalData.set(target, Double.parseDouble(this.change.substring(1)));
        }else{
            originalData.set(target, originalData.get(target) + Double.parseDouble(this.change));
        }
        
    }
    
    public JCpSimParameter getTarget() {
        return target;
    }

    public String getChange() {
        return change;
    }

    public boolean isNotifyChange() {
        return notifyChange;
    }

    public String getSource() {
        return source;
    }

    public boolean isAutoRetractable() {
        return true;
    }

    public int getSalience() {
        return salience;
    }
    
}
