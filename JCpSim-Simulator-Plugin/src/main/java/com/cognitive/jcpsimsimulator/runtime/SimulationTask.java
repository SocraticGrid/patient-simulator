/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.runtime;

import com.cognitive.SimulationExecutor;
import com.cognitive.jcpsimsimulator.config.database.DatabaseConfiguration;
import com.cognitive.template.SimulationTemplateEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.JCpSimMgmt;
import org.jcpsim.jmx.JCpSimTopMenuMgmtMBean;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Cancellable;

/**
 *
 * @author esteban
 */
public final class SimulationTask implements Runnable, SimulationExecutor.SimulationListener {

    private static final Logger LOG = Logger.getLogger(SimulationTask.class.getName());
    
    private final SimulationExecutor executor;
    private final ProgressHandle ph;
    private final JCpSimDataManager jcpSimClient;
    private final JCpSimDataManager jcpSimAuxClient;
    private final String simulationId;
    private int currentSimulationTime = 0;
    private EntityManager em;
    private final JCpSimTopMenuMgmtMBean topMenuMBean;

    public static interface SimulationTaskListener{
        public void onTaskCompleted();
        public void onError(Throwable t);
    }
    
    SimulationTaskListener taskListener;
    
    public SimulationTask(String simulationTaskId, SimulationTemplateEngine engine, JCpSimTopMenuMgmtMBean topMenuMBean, Map<String, Object> globals, SimulationTaskListener taskListener) {
        try {
            this.simulationId = simulationTaskId;
            this.setTaskListener(taskListener);
            this.topMenuMBean = topMenuMBean;
            
            this.executor = new SimulationExecutor(engine, this);
            
            if (globals != null){
                for (Map.Entry<String, Object> entry : globals.entrySet()) {
                    this.executor.addGlobal(entry.getKey(), entry.getValue());
                }
            }
            
            jcpSimClient = new JCpSimPollingClient(Global.MODE.SIM.getJMXUrl());
            
            JCpSimPollingClient ac = null;
            try{
                ac = new JCpSimPollingClient(Global.MODE.AUX.getJMXUrl(), JCpSimMgmt.OBJECT_NAME+"_"+Global.MODE.AUX);
            } catch (Exception e){
                //Aux is not mandatory
            }
            
            jcpSimAuxClient = ac != null? ac:null;
            
            this.ph = ProgressHandleFactory.createHandle("Running Simulation", new Cancellable() {
                @Override
                public boolean cancel() {
                    try{
                        if (executor.isPaused()){
                            executor.resume();
                        }
                        executor.stop();
                        SimulationTask.this.topMenuMBean.simulationStopped(simulationId);
                        ph.finish();
                    } catch (Exception e){
                        notifyError(e);
                    }
                    return true;
                }
            });
        } catch (Exception ex) {
            this.notifyError(ex);
            throw new IllegalStateException(ex);
        }
    }
    
    public void setTaskListener(SimulationTaskListener listener){
        taskListener = listener;
    }

    public void startInNewThread() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try{
            Map<String, String> connectionConfiguration = DatabaseConfiguration.getInstance().getConfiguration();
            this.em = Persistence.createEntityManagerFactory("SimulatorPU", connectionConfiguration).createEntityManager();

            em.getTransaction().begin();
            try{
                ph.start((int) executor.getSimulationExecutionTime()*1000);
                topMenuMBean.simulationStarted(simulationId);
                executor.executeJMX();
                ph.finish();
                topMenuMBean.simulationStopped(simulationId);
                StatusDisplayer.getDefault().setStatusText("Simulation Completed");
                em.getTransaction().commit();
            } catch(RuntimeException e){
              em.getTransaction().rollback();
              throw e;
            } finally{
                if (taskListener != null){
                    taskListener.onTaskCompleted();
                }
                this.em.close();
            }
        } catch (Exception e){
            notifyError(e);
        }
    }

    @Override
    public void onStep(int time) {
        currentSimulationTime = time;
        ph.progress("t="+currentSimulationTime+" ms", time);
    }

    @Override
    public void onException(Exception e) {
        notifyError(e);
    }

    @Override
    public void onPause() {
        ph.setDisplayName("Simulation Paused");
    }
    
    public boolean isPaused() {
        return executor.isPaused();
    }

    public void requestPause() {
        executor.pause();
        ph.setDisplayName("Pause Requested");
    }

    public void resume() {
        //copy aux ventilator values to JCpSim
        if (this.jcpSimAuxClient != null){
            JCpSimData auxData = this.jcpSimAuxClient.getData();
            for (JCpSimParameter p : JCpSimParameter.values()) {
                if (p.name().startsWith("V_")){
                    this.jcpSimClient.set(p, auxData.get(p));
                }
            }
        }
        
        ph.setDisplayName("Running Simulation");
        executor.resume();
    }
    
    public void takeSnapshot(){
        try{
            JCpSimData data = this.jcpSimClient.getData();
            JCpSimDataSample sample = JCpSimDataSample.fromJCpSimData(simulationId, data);

            //If there is an aux client copy the 'expected' values for ventilator 
            //parameters
            if (jcpSimAuxClient != null){
                List<Expectation> expectations = new ArrayList<Expectation>();
                JCpSimData auxData = this.jcpSimAuxClient.getData();
                for (JCpSimParameter p : JCpSimParameter.values()) {
                    if (p.name().startsWith("V_")){
                        expectations.add(new Expectation(UUID.randomUUID().toString(), p.name(), auxData.get(p)));
                    }
                }
                sample.setExpectations(expectations);
            }

            sample.setSimulationTime(currentSimulationTime);
            this.em.persist(sample);
        } catch (Exception e){
            this.notifyError(e);
        }
    }
    
    private void notifyError(Throwable t){
        if (this.taskListener != null){
            this.taskListener.onError(t);
        }else{
            LOG.log(Level.SEVERE, "Error while running simulation", t);
        }
    }
    
}
