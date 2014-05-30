/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.patientsimulator.recommendation;

import com.cognitive.bp.poc.model.AbnormalBucket;
import com.cognitive.bp.poc.model.AlertRequest;
import com.cognitive.bp.poc.model.PatientMedicationEvent;
import com.cognitive.bp.poc.model.PeakJCpSimData;
import com.cognitive.bp.poc.recommendation.RecommendationSystemFactory;
import com.cognitive.bp.poc.recommendation.RecommendationSystem;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.client.JCpSimNotificationClient;

/**
 *
 * @author esteban
 */
public class RecommendationContextListener implements ServletContextListener {

    public static JCpSimNotificationClient jCpSimNotificationClient;
    public static RecommendationSystem recommendationSystem;
    private final static Object lock = new Object();
    private final Semaphore recommendationSystemLock = new Semaphore(1);
    private JCpSimData maxDataInBuffer;
    private boolean bufferProcessorRunning;
    private ScheduledExecutorService bufferProcessorThreadExecutor;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {

            synchronized (lock) {

                if (recommendationSystem != null) {
                    return;
                }
                recommendationSystem = new RecommendationSystemFactory().build();

                recommendationSystem.addEventListener(new RecommendationSystem.RecommendationSystemEventListener() {

                    @Override
                    public void newAbnormalBucket(AbnormalBucket abnormalBucket) {
                        try {
                            JsonObject object = new JsonObject();
                            object.addProperty("type", "AbnormalBucket");
                            object.addProperty("start", abnormalBucket.getStart());
                            object.addProperty("end", abnormalBucket.getEnd());
                            object.addProperty("duration", abnormalBucket.getDuration());
                            object.addProperty("sd", abnormalBucket.getSd());

                            RecommendationConsoleWebSocket.broadcast(object);
                        } catch (IOException ex) {
                            Logger.getLogger(RecommendationContextListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    @Override
                    public void newAlertRequest(AlertRequest alertRequest) {
                        try {
                            JsonObject object = new JsonObject();
                            object.addProperty("type", "AlertRequest");
                            object.addProperty("message", alertRequest.getMessage());

                            RecommendationConsoleWebSocket.broadcast(object);
                        } catch (IOException ex) {
                            Logger.getLogger(RecommendationContextListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                });

                String jmxURL = sce.getServletContext().getInitParameter("jmx.url");
                String jmxObject = sce.getServletContext().getInitParameter("jmx.object");
                String pollTime = sce.getServletContext().getInitParameter("recommendation.poll.time");


                jCpSimNotificationClient = new JCpSimNotificationClient(jmxURL, jmxObject, new JCpSimNotificationClient.JCpSimNotificationListener() {

                    @Override
                    public void onData(JCpSimData data) {
//                        doRegularPushClient(data);
//                        doRegularPushClientDiscardingConcurrntData(data);
                        doBufferingPushClient(data);
                    }
                });

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doRegularPushClient(JCpSimData data) {
        recommendationSystem.notifyData(data);
    }

    private void doRegularPushClientDiscardingConcurrntData(JCpSimData data) {
        if (!recommendationSystemLock.tryAcquire()) {
            //discard concurrent data
            return;
        }
        try {
            recommendationSystem.notifyData(data);
        } finally {
            recommendationSystemLock.release();
        }
    }

    private void doBufferingPushClient(JCpSimData data)  {

        try {
            recommendationSystemLock.acquire();
            try {
                if (maxDataInBuffer == null || data.get(JCpSimParameter.AA_O_PRESP) > maxDataInBuffer.get(JCpSimParameter.AA_O_PRESP)) {
                    maxDataInBuffer = data;
                }
                startBufferProcessor();
            } finally {
                recommendationSystemLock.release();
            }
        }   catch (InterruptedException ex) {
            Logger.getLogger(RecommendationContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Runnable bufferProcessorJob = new Runnable() {

        @Override
        public void run() {
            PeakJCpSimData peak = null;
            try {
                recommendationSystemLock.acquire();
                if (maxDataInBuffer == null){
                    return;
                }
                peak = new PeakJCpSimData(maxDataInBuffer.getData());
                peak.setInBucket(false);
                maxDataInBuffer = null;
            } catch (InterruptedException ex) {
                Logger.getLogger(RecommendationContextListener.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                recommendationSystemLock.release();
                bufferProcessorThreadExecutor.schedule(bufferProcessorJob, 5, TimeUnit.SECONDS);
            }
            System.out.println("Notifying Session about a Peak: "+peak);
            recommendationSystem.notifyPeak(peak);
            
        }
    };

    private synchronized void startBufferProcessor() {
        if (bufferProcessorRunning) {
            return;
        }

        bufferProcessorThreadExecutor = Executors.newScheduledThreadPool(1);

        bufferProcessorThreadExecutor.schedule(bufferProcessorJob, 5, TimeUnit.SECONDS);

        bufferProcessorRunning = true;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (jCpSimNotificationClient != null) {
            jCpSimNotificationClient.stop();
        }
    }

}
