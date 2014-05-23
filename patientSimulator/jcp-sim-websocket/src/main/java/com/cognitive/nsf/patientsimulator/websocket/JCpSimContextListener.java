/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
