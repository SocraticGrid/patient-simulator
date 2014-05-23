/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.patientsimulator.jcpsim;

/**
 *
 * @author esteban
 */
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

        @Override
        public void run() {
            listener.onDataReceived(data);
        }
    };

    private class DataGathererJob implements Runnable {

        private int errorCount = 0;
        private static final int ERROR_THRESHOLD = 5;

        @Override
        public void run() {
            if (running) {
                try {
                    //get data from JCpSim
                    final JCpSimData data = dataProvider.getData();

                    //notify listeners
                    for (final JCpSimDataReceivedEventListener listener : eventListeners) {
                        eventListenersNotificationExecutor.execute(new EventListenerNotifier(listener, data));
                    }

                    errorCount = 0;
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error retrieving data from JCpSim", e);
                    errorCount++;
                } finally {
                    //schedule new execution
                    if (errorCount >= ERROR_THRESHOLD) {
                        LOG.log(Level.SEVERE, "Error threashold ({0}) reached. Stopping data gatherer.", ERROR_THRESHOLD);
                        JCpSimDataGatherer.this.stop();
                    } else {
                        dataGathererThreadExecutor.schedule(this, sampleRate, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }
    private long sampleRate;
    private JCpSimDataProviderFactory dataProviderFactory;
    private JCpSimDataProvider dataProvider;
    private boolean running;
    private ScheduledExecutorService dataGathererThreadExecutor;
    private ExecutorService eventListenersNotificationExecutor;
    protected Set<JCpSimDataReceivedEventListener> eventListeners = new CopyOnWriteArraySet<>();

    public JCpSimDataGatherer(JCpSimDataProviderFactory dataProviderFactory) {
        this(dataProviderFactory, DEFAULT_SAMPLE_RATE);
    }

    public JCpSimDataGatherer(JCpSimDataProviderFactory dataProviderFactory, long sampleRate) {
        this.dataGathererThreadExecutor = Executors.newScheduledThreadPool(1);
        this.eventListenersNotificationExecutor = Executors.newFixedThreadPool(5);
        this.dataProviderFactory = dataProviderFactory;
        this.sampleRate = sampleRate;
    }

    public void start() {
        synchronized (this) {
            if (running) {
                throw new IllegalStateException("Already running");
            }

            try {
                this.dataProvider = this.dataProviderFactory.createProvider();
                dataGathererThreadExecutor.schedule(new DataGathererJob(), sampleRate, TimeUnit.MILLISECONDS);
                running = true;
            } catch (Exception ex) {
                throw new RuntimeException("Unable to create JCpSim Client", ex);
            }
        }
    }

    public synchronized void stop() {
        this.dataProvider = null;
        this.running = false;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public boolean addEventListener(JCpSimDataReceivedEventListener e) {
        return eventListeners.add(e);
    }

    public boolean removeEventListener(JCpSimDataReceivedEventListener o) {
        return eventListeners.remove(o);
    }
}
