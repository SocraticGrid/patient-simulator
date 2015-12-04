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

/**
 *
 * @author esteban
 */
public class SimulationClockToken  implements InternalToken{
    
    private final long timeMillis;
    /**
     * Difference between this clock token and the previous one.
     */
    private final long timeDiffMillis;
    private final JCpSimData associatedTo;
    
    public SimulationClockToken(long timeMillis, long timeDiffMillis, JCpSimData associatedTo) {
        this.timeMillis = timeMillis;
        this.associatedTo = associatedTo;
        this.timeDiffMillis = timeDiffMillis;
    }
    
    public long getTimeMillis() {
        return timeMillis;
    }

    public JCpSimData getAssociatedTo() {
        return associatedTo;
    }
    
    public boolean isAutoRetractable() {
        return false;
    }

    public long getTimeDiffMillis() {
        return timeDiffMillis;
    }
    
}
