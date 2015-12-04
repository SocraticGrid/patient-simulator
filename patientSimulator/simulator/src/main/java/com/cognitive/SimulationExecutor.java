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
package com.cognitive;

import com.cognitive.data.EvictDataToken;
import com.cognitive.data.SimulationClockToken;
import com.cognitive.data.SuspendExecutionToken;
import com.cognitive.template.SimulationTemplateEngine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
import org.drools.core.time.SessionPseudoClock;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.JCpSimClockMgmt;
import org.jcpsim.jmx.JCpSimClockMgmtMBean;
import org.jcpsim.jmx.JCpSimCustomRespiratorMgmt;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 *
 * @author esteban
 */
public class SimulationExecutor {

    private final SimulationTemplateEngine engine;
    private boolean paused = false;
    private boolean stop = false;
    private FactHandle suspendExecutionToken;
    private KieBase kbase;
    private SimulationListener simulationListener;
    
    private final List<Object> initialFacts = new ArrayList<Object>();
    private final Map<String, Object> globals = new HashMap<String, Object>();
    private JCpSimDataManager simClient;
    
    private long step = 100;
    
    private Map<JCpSimParameter, Double> initialParameterValues = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
    private KieSession ksession;
    private boolean debug;
    private boolean randomizeStep;

    public static interface SimulationListener {

        void onStep(int time);
        void onException(Exception e);
        void onPause();
        void onTermination();
    }
    
    public SimulationExecutor(SimulationTemplateEngine engine, SimulationListener simulationListener) {
        this.engine = engine;
        this.simulationListener = simulationListener;
        this.createKBase();
    }
    
    public SimulationExecutor(SimulationTemplateEngine engine, SimulationListener simulationListener, long step) {
        this.engine = engine;
        this.simulationListener = simulationListener;
        this.step = step;
        this.createKBase();
    }

    public void executeJMX() {
        this.executeJMX(Global.MODE.SIM.getJMXUrl());
    }

    public void executeJMX(String simConnectionURL) {
        try {
            JCpSimPollingClient simClient = new JCpSimPollingClient(simConnectionURL, JCpSimCustomRespiratorMgmt.OBJECT_NAME+"_"+Global.MODE.SIM);
            this.execute(simClient);
        } catch (IOException ex) {
            Logger.getLogger(SimulationExecutor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(SimulationExecutor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(SimulationExecutor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public void execute(final JCpSimDataManager simClient) {
        
        this.simClient = simClient;

        KieSessionConfiguration conf = KieServices.Factory.get().newKieSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));
        ksession = kbase.newKieSession(conf, null);

        if (debug){
            DroolsEventListener eventListener = new DroolsEventListener();
            ksession.addEventListener((AgendaEventListener)eventListener);
            ksession.addEventListener((RuleRuntimeEventListener)eventListener);
        }

        ksession.setGlobal("jCPSimClient", simClient);

        final SessionPseudoClock clock = ksession.getSessionClock();
        ksession.setGlobal("clock", clock);
        
        ksession.setGlobal("sampleRate", step);
        
        for (Map.Entry<String, Object> entry : globals.entrySet()) {
            ksession.setGlobal(entry.getKey(), entry.getValue());
        }

        for (Object object : initialFacts) {
            ksession.insert(object);
        }

        try {
            JMXServiceURL serviceUrl = new JMXServiceURL(Global.MODE.SIM.getJMXUrl());
            JMXConnector jmxc = JMXConnectorFactory.connect(serviceUrl, null);

            MBeanServerConnection connection = jmxc.getMBeanServerConnection();

            final JCpSimClockMgmtMBean jcpsimClock = JMX.newMBeanProxy(connection, new ObjectName(JCpSimClockMgmt.OBJECT_NAME+"_"+Global.MODE.SIM),
                    JCpSimClockMgmtMBean.class, true);

            new Thread(new Runnable() {
                boolean pauseAlreadyNotified;

                @Override
                public void run() {
                    try {
                        
                        //set initial parameter values
                        for (Map.Entry<JCpSimParameter, Double> entry : initialParameterValues.entrySet()) {
                            if (entry.getKey() == JCpSimParameter.TIME){
                                //time has a particular treatment
                                jcpsimClock.setTime(entry.getValue().longValue());
                            } else{
                                simClient.set(entry.getKey(), entry.getValue());
                            }
                        }
                        
                        int t = 0;
                        long offset = jcpsimClock.getTime();
                        long lastTime = -999;
                        while (!stop) {
                            JCpSimData data = simClient.getData();
                            
                            if (data.getTime() != lastTime){
                                lastTime = data.getTime();
                                
                                paused = false;
                                pauseAlreadyNotified = false;
                                long timeDelta = data.getTime()-clock.getCurrentTime()-offset;
                                clock.advanceTime(timeDelta, TimeUnit.MILLISECONDS);
                                
                                t+=timeDelta;
                                ksession.insert(new SimulationClockToken(t, timeDelta, data));
                                ksession.insert(data);
                                if (simulationListener != null) {
                                    simulationListener.onStep(t);
                                }
                            } else {
                                paused = true;
                                if (!pauseAlreadyNotified) {
                                    //notify the listener
                                    if (simulationListener != null){
                                        simulationListener.onPause();
                                    }
                                    
                                    //evict data from session
                                    ksession.insert(new EvictDataToken());
                                    pauseAlreadyNotified = true;
                                }
                            }
                            Thread.sleep(SimulationExecutor.this.getTimeToSleep());
                        }
                        ksession.halt();

                    //} catch (InterruptedException ex) {
                    //    Logger.getLogger(SimulationExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex){
                        if (simulationListener != null){
                            //stop();
                            simulationListener.onException(ex);
                        }
                    }
                }
                
            }).start();


            ksession.fireUntilHalt();
            stop = true;
            
            if (this.simulationListener != null){
                this.simulationListener.onTermination();
            }
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long getRandom(long max , long min){
        long ii = -min + (long) (Math.random() * ((max - (-min)) + 1));
        return ii;
    }
    
    private long getTimeToSleep(){
        if (this.randomizeStep){
            return step+(this.getRandom(step/10, -step/10));
        } else{
            return step;
        }
    }
    
    public long getSimulationExecutionTime() {
        //TODO: create a propper drools -> milliseconds convertor
        int time = Integer.parseInt(engine.getSimulationTime().substring(0, engine.getSimulationTime().length() - 1));
        return time;
    }

    public void addInitialParameterValue(JCpSimParameter parameter, double value){
        this.initialParameterValues.put(parameter, value);
    }
    
    public void removeInitialParameterValue(JCpSimParameter parameter){
        this.initialParameterValues.remove(parameter);
    }
    
    public void clearInitialParameterValues(){
        this.initialParameterValues.clear();
    }
    
    public void pause() {
        simClient.requestPause();
    }

    public void resume() {
        simClient.resume();
    }
    
    public synchronized void suspend() {
        
        if (this.suspendExecutionToken != null){
            return;
        }
        
        this.suspendExecutionToken = this.ksession.insert(new SuspendExecutionToken());
    }

    public synchronized void unsuspend() {
        if (this.suspendExecutionToken == null){
            return;
        }
        
        this.ksession.retract(this.suspendExecutionToken);
        this.suspendExecutionToken = null;
    }

    public void stop() {
        this.stop = true;
    }

    public boolean isPaused() {
        return this.paused;
    }
    
    public void addInitialFact(Object initialFact){
        this.initialFacts.add(initialFact);
    }
    
    public void removeInitialFact(Object initialFact){
        this.initialFacts.remove(initialFact);
    }
    
    public void addGlobal(String name, Object value){
        this.globals.put(name, value);
    }
    
    public void removeInitialFact(String name){
        this.globals.remove(name);
    }

    public void setRandomizeStep(boolean randomizeStep){
        this.randomizeStep = randomizeStep;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    private void createKBase() {
        
        KieHelper helper = new KieHelper();
        
        String simulationDRL = engine.createSimulationDRL();
        helper.addResource(ResourceFactory.newClassPathResource("rules/core.drl"), ResourceType.DRL);
        helper.addResource(ResourceFactory.newByteArrayResource(simulationDRL.getBytes()), ResourceType.DRL);

        Results results = helper.verify();
        
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.printf("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
            }
            System.out.println("\n\n"+simulationDRL+"\n\n");
            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        KieBaseConfiguration config = KieServices.Factory.get().newKieBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        config.setOption( DeclarativeAgendaOption.ENABLED );
        
        this.kbase = helper.build(config);
    }
}
