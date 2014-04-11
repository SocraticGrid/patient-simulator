/*
 * JCpSimMgmtMBean.java
 *
 * Created on September 5, 2012, 3:53 PM
 */
package org.jcpsim.jmx;

import org.jcpsim.clock.Clock;
import org.jcpsim.run.Global;

/**
 * Interface JCpSimMgmtMBean
 *
 * @author esteban
 */
public class JCpSimClockMgmt implements JCpSimClockMgmtMBean{
    
    public static final String OBJECT_NAME = "org.jcpsim:type=ClockMgmt";
    
    private final Clock clock;

    public JCpSimClockMgmt(Clock clock) {
        this.clock = clock;
    }
    
    
    public void setTime(long currentTime){
        clock.setCurrentTime(currentTime);
    }

    public long getTime() {
        return clock.getCurrentTime();
    }

}
