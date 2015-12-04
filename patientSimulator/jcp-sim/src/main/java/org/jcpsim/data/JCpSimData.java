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
package org.jcpsim.data;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author esteban
 */
public interface JCpSimData extends Serializable{
    
    public void setTime(long value);
    public long getTime();
    public double get(JCpSimParameter parameter);
    public void set(JCpSimParameter paramter, Double value);
    public Map<JCpSimParameter, Double> getData();
    public boolean hasEquivalentInputParameters(JCpSimData data);
    public boolean hasEquivalentGasOutputParameters(JCpSimData data);
}

