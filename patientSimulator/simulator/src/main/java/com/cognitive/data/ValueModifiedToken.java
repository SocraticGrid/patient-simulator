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

import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ValueModifiedToken  implements InternalToken{
    private final JCpSimParameter target;
    private final Double oldValue;
    private final Double newValue;
    private final String source;

    public ValueModifiedToken(JCpSimParameter target, Double oldValue, Double newValue, String source) {
        this.target = target;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.source = source;
    }

    public JCpSimParameter getTarget() {
        return target;
    }

    public Double getOldValue() {
        return oldValue;
    }

    public Double getNewValue() {
        return newValue;
    }

    public String getSource() {
        return source;
    }
    
    public boolean isAutoRetractable() {
        return true;
    }

    @Override
    public String toString() {
        return "ValueModifiedToken{" + "target=" + target + ", oldValue=" + oldValue + ", newValue=" + newValue + ", source=" + source + '}';
    }
    
}
