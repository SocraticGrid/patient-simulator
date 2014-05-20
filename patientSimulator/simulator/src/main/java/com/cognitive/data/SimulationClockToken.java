/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
