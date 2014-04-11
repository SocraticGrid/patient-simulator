/*
 * JCpSimMgmtMBean.java
 *
 * Created on September 5, 2012, 3:53 PM
 */
package org.jcpsim.jmx;

/**
 * Interface JCpSimMgmtMBean
 *
 * @author esteban
 */
public interface JCpSimClockMgmtMBean{
    public void setTime(long currentTime);
    public long getTime();
}
