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
package com.cognitive.nsf.management.jcpsim;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataProvider;

/**
 *
 * @author esteban
 */
public class JCpSimDataGatherer {

    public static final long DEFAULT_SAMPLE_RATE = 100;
    private static final Logger LOG = Logger.getLogger(JCpSimDataGatherer.class.getName());

    private static class EventListenerNotifier implements Runnable {

        private final JCpSimDataReceivedEventListener listener;
        private final JCpSimData data;

        public EventListenerNotifier(JCpSimDataReceivedEventListener listener, JCpSimData data) {
            this.listener = listener;
            this.data = data;
        }

        public void run() {
            listener.onDataReceived(data);
        }
    };

    private class DataGathererJob implements Runnable {

        public void run() {
            if (running) {
                try {
                    //get data from JCpSim
                    final JCpSimData data = dataProvider.getData();

                    //notify listeners
                    for (final JCpSimDataReceivedEventListener listener : eventListeners) {
                        eventListenersNotificationExecutor.execute(new EventListenerNotifier(listener, data));
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error retrieving data from JCpSim", e);
                } finally {
                    //schedule new execution
                    dataGathererThreadExecutor.schedule(this, sampleRate, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    private long sampleRate;
    private JCpSimDataProvider dataProvider;
    private boolean running;
    private ScheduledExecutorService dataGathererThreadExecutor = Executors.newScheduledThreadPool(1);
    private ExecutorService eventListenersNotificationExecutor = Executors.newFixedThreadPool(5);
    protected Set<JCpSimDataReceivedEventListener> eventListeners = new CopyOnWriteArraySet<JCpSimDataReceivedEventListener>();

    public JCpSimDataGatherer(JCpSimDataProvider dataProvider) {
        this(dataProvider, DEFAULT_SAMPLE_RATE);
    }

    public JCpSimDataGatherer(JCpSimDataProvider dataProvider, long sampleRate) {
        this.dataProvider = dataProvider;
        this.sampleRate = sampleRate;
    }

    public void start() {
        synchronized (this) {
            if (running) {
                throw new IllegalStateException("Already running");
            }
            running = true;
        }

        try {
            dataGathererThreadExecutor.schedule(new DataGathererJob(), sampleRate, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create JCpSim Client", ex);
        }

    }

    public synchronized void stop() {
        this.running = false;
    }

    public boolean addEventListener(JCpSimDataReceivedEventListener e) {
        return eventListeners.add(e);
    }

    public boolean removeEventListener(JCpSimDataReceivedEventListener o) {
        return eventListeners.remove(o);
    }
}
