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
public interface JCpSimTopMenuMgmtMBean{
    public void simulationStarted(String simulationId);
    public void simulationStopped(String simulationId);
}
