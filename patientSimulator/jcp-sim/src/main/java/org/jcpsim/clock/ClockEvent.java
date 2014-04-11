/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.clock;

/**
 *
 * @author esteban
 */
public class ClockEvent {
    
    public static enum TYPE {
        CLOCK_PAUSED,
        CLOCK_RESUMED;
    }
    
    private final TYPE type;
    private final long currentTime;

    public ClockEvent(TYPE type, long currentTime) {
        this.type = type;
        this.currentTime = currentTime;
    }

    public TYPE getType() {
        return type;
    }

    public long getCurrentTime() {
        return currentTime;
    }

}
