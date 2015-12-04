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
package org.jcpsim.jmx.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.jmx.JCpSimCustomRespiratorMgmt;
import org.jcpsim.jmx.JCpSimDataNotification;
import org.jcpsim.run.Global;

/**
 *
 * @author esteban
 */
public class JCpSimNotificationClient {
    public static interface JCpSimNotificationListener{
        public void onData(JCpSimData data);
    }
            
    private final String url;
    private final String objectName;
    private final NotificationListener listener;
    private MBeanServerConnection connection;
    private boolean running;
    
    public JCpSimNotificationClient(String url, JCpSimNotificationListener listener) {
        this(url, JCpSimCustomRespiratorMgmt.OBJECT_NAME+"_"+Global.MODE.SIM, listener);
    }
    
    public JCpSimNotificationClient(String url, String objectName, final JCpSimNotificationListener listener) {
        this.url = url;
        this.objectName = objectName;
        this.listener = new NotificationListener(){

            public void handleNotification(Notification notification, Object handback) {
                if(!(notification instanceof JCpSimDataNotification)){
                    throw new IllegalArgumentException("Unexpected Notification type "+notification.getClass()+". I was expecting an instanceo of org.jcpsim.jmx.JCpSimDataNotification");
                }
                listener.onData(((JCpSimDataNotification)notification).getData());
            }
        };
    }
    
    public synchronized void start() {
        try {
            if (running){
                return;
            }
            
            JMXServiceURL serviceUrl = new JMXServiceURL(url);
            JMXConnector jmxc = JMXConnectorFactory.connect(serviceUrl, null);
            connection = jmxc.getMBeanServerConnection();
            connection.addNotificationListener(new ObjectName(objectName), listener, null, "");
            
            running = true;
        } catch (Exception ex) {
            throw new IllegalStateException("Error starting JCpSimNotificationListener", ex);
        }
    }
    
    public synchronized void stop(){
        if (connection != null){
            try{
                connection.removeNotificationListener(new ObjectName(objectName), listener);
            } catch (Exception e){
                
            }
        }
        running = false;
    }
    
    public synchronized boolean isRunning(){
        return this.running;
    }
    
    
}
