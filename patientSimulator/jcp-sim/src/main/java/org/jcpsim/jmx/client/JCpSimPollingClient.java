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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.JCpSimCustomRespiratorMgmt;
import org.jcpsim.jmx.JCpSimCustomRespiratorMgmtMBean;
import org.jcpsim.run.Global;

/**
 *
 * @author esteban
 */
public class JCpSimPollingClient implements JCpSimDataManager {
    
    private static final Logger LOG = Logger.getLogger(JCpSimPollingClient.class.getName());

    private JMXConnector jmxc;
    
    JCpSimCustomRespiratorMgmtMBean mbean = null;
    
    //service:jmx:rmi:///jndi/rmi://:9999/jmxrmi
    public JCpSimPollingClient(String url) throws IOException, MalformedObjectNameException, InstanceNotFoundException{
        this(url, JCpSimCustomRespiratorMgmt.OBJECT_NAME+"_"+Global.MODE.SIM);
    }
    
    public JCpSimPollingClient(String url, String objectName) throws IOException, MalformedObjectNameException, InstanceNotFoundException{
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        jmxc = JMXConnectorFactory.connect(serviceUrl, null);
        MBeanServerConnection connection = jmxc.getMBeanServerConnection();
        this.mbean = JMX.newMBeanProxy(connection, new ObjectName(objectName), 
                                          JCpSimCustomRespiratorMgmtMBean.class, true);
    }

    public JCpSimData getData() {
        return mbean.getData();
    }

    public String getDataAsString() {
        return mbean.getDataAsString();
    }
    
    public void set(JCpSimParameter parameter, double value) {
        try{
            mbean.set(parameter, value);
        } catch (IllegalArgumentException e){
            LOG.log(Level.WARNING, "Trying to set a R/O value: {0}", parameter);
        }
    }

    public void requestPause() {
        mbean.requestPause();
    }

    public void resume() {
        mbean.resume();
    }
    
    public void dispose() throws IOException{
        jmxc.close();
    }

}
