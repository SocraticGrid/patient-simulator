/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitive.nsf.patientsimulator.recommendation;

import com.cognitive.nsf.patientsimulator.jcpsim.JCpSimDataGatherer;
import com.cognitive.nsf.patientsimulator.jcpsim.JCpSimDataProviderFactory;
import com.cognitive.nsf.patientsimulator.jcpsim.JCpSimDataReceivedEventListener;
import com.cognitive.nsf.patientsimulator.jcpsim.JMXJCpSimDataProviderFactory;
import com.cognitive.bp.poc.recommendation.RecommendationSystemFactory;
import com.cognitive.bp.poc.recommendation.RecommendationSystem;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jcpsim.data.JCpSimData;

/**
 *
 * @author esteban
 */
public class RecommendationContextListener implements ServletContextListener {
    public static JCpSimDataGatherer jCpSimDataGatherer;
    public static RecommendationSystem recommendationSystem;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {

            recommendationSystem = new RecommendationSystemFactory().build();
            
            String jmxURL = sce.getServletContext().getInitParameter("jmx.url");
            String jmxObject = sce.getServletContext().getInitParameter("jmx.object");
            String pollTime = sce.getServletContext().getInitParameter("recommendation.poll.time");
            
            
            long sampleRate = Long.parseLong(pollTime);
            
            
            JCpSimDataProviderFactory dataProviderFactory = new JMXJCpSimDataProviderFactory(jmxURL, jmxObject);
            jCpSimDataGatherer = new JCpSimDataGatherer(dataProviderFactory, sampleRate);
            
            jCpSimDataGatherer.addEventListener(new JCpSimDataReceivedEventListener() {

                @Override
                public void onDataReceived(JCpSimData data) {
                    recommendationSystem.notifyData(data);
                }
            });
            
            jCpSimDataGatherer.start();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (jCpSimDataGatherer != null){
            jCpSimDataGatherer.stop();
        }
    }
    
}
