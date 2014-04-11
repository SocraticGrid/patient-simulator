/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.jcpsim;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataProvider;

/**
 *
 * @author esteban
 */
public class JCpSimDataGatherer {
    public static final long DEFAULT_SAMPLE_RATE = 100;

    private class DataGathererJob implements Runnable{
        
        public void run() {
            if (running){
                //get data from JCpSim
                final JCpSimData data = dataProvider.getData();
                
                //notify listeners
                for (final JCpSimDataReceivedEventListener listener : eventListeners) {
                    eventListenersNotificationExecutor.execute(new Runnable() {
                        public void run() {
                            listener.onDataReceived(data);
                        }
                    });
                }
                
                //schedule new execution
                dataGathererThreadExecutor.schedule(this, sampleRate, TimeUnit.MILLISECONDS);
            }
        }
        
    }

    private long sampleRate;
    private JCpSimDataProvider dataProvider;
    
    private boolean running;
    private ScheduledExecutorService dataGathererThreadExecutor = Executors.newScheduledThreadPool(1);
    private ExecutorService eventListenersNotificationExecutor = Executors.newFixedThreadPool(5);

    private Set<JCpSimDataReceivedEventListener> eventListeners = new CopyOnWriteArraySet<JCpSimDataReceivedEventListener>();
    
    public JCpSimDataGatherer(JCpSimDataProvider dataProvider){
        this(dataProvider, DEFAULT_SAMPLE_RATE);
    }
    
    public JCpSimDataGatherer(JCpSimDataProvider dataProvider, long sampleRate){
        this.dataProvider = dataProvider;
        this.sampleRate = sampleRate;
    }
    
    public void start(){
        synchronized(this){
            if (running){
                throw new IllegalStateException("Already running");
            }
            running = true;
        }
        
        try {
            dataGathererThreadExecutor.schedule(new DataGathererJob(), sampleRate, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create JCpSim Client",ex);
        }
        
    }
    
    public synchronized void stop(){
        this.running = false;
    }

    public boolean addEventListener(JCpSimDataReceivedEventListener e) {
        return eventListeners.add(e);
    }

    public boolean removeEventListener(JCpSimDataReceivedEventListener o) {
        return eventListeners.remove(o);
    }
    
    
}
