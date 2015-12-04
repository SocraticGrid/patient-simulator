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
package com.cognitive.nsf.patientsimulator.websocket;

import com.cognitive.nsf.patientsimulator.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.patientsimulator.jcpsim.JCpSimDataProviderFactory;
import com.cognitive.nsf.patientsimulator.jcpsim.JCpSimDataReceivedEventListener;
import com.cognitive.nsf.patientsimulator.jcpsim.JMXJCpSimDataProviderFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jcpsim.data.JCpSimData;

/**
 * Web application lifecycle listener.
 * This class starts JCpSimDataGatherer when webapp is deployed
 * @author esteban
 */
public class JCpSimContextListener implements ServletContextListener {
    public static JCpSimDataGatherer jCpSimDataGatherer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            
            String jmxURL = sce.getServletContext().getInitParameter("jmx.url");
            String jmxObject = sce.getServletContext().getInitParameter("jmx.object");
            String pollTime = sce.getServletContext().getInitParameter("jmx.poll.time");
            
            
            long sampleRate = Long.parseLong(pollTime);
            
            
            JCpSimDataProviderFactory dataProviderFactory = new JMXJCpSimDataProviderFactory(jmxURL, jmxObject);
            jCpSimDataGatherer = new JCpSimDataGatherer(dataProviderFactory, sampleRate);
            
            jCpSimDataGatherer.addEventListener(new JCpSimDataReceivedEventListener() {

                @Override
                public void onDataReceived(JCpSimData data) {
                    try {
                        JCpSimWebSocket.broadcast(data);
                    } catch (IOException ex) {
                        Logger.getLogger(JCpSimContextListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            jCpSimDataGatherer.start();
            
            
            
        } catch (Exception ex) {
            
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (jCpSimDataGatherer != null){
            jCpSimDataGatherer.stop();
        }
    }
}
