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
