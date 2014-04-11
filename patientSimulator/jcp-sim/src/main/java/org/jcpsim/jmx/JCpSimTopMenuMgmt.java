/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.jmx;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import org.jcpsim.gui.TopMenu;


public class JCpSimTopMenuMgmt extends NotificationBroadcasterSupport implements JCpSimTopMenuMgmtMBean {
    
    public static final String OBJECT_NAME =  "org.jcpsim:type=TopMenu";
    
    private int sequenceNumber;
    private final TopMenu topMenu;

    public JCpSimTopMenuMgmt(TopMenu topMenu) {
        this.topMenu = topMenu;
    }
    
    public void simulationStarted(String simulationId) {
        this.topMenu.onSimulationStarted(simulationId);
    }

    public void simulationStopped(String simulationId) {
        this.topMenu.onSimulationStopped(simulationId);
    }
    
    public void requestSnapshot(String simulationId){
        Notification n = new Notification("Snapshot Request", this, sequenceNumber++, simulationId);
        this.sendNotification(n);
    }
    
}
