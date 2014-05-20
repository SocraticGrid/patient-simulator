/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.executor;

import com.cognitive.SimulationExecutor;
import com.cognitive.jcpsimsimulator.runtime.Expectation;
import com.cognitive.jcpsimsimulator.runtime.JCpSimDataSample;
import com.cognitive.template.FreeFormRuleTemplate;
import com.cognitive.template.SimulationRuleTemplate;
import com.cognitive.template.SimulationTemplateEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.persistence.EntityManager;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.gui.TopMenu;
import org.jcpsim.jmx.JCpSimCustomRespiratorMgmt;
import org.jcpsim.jmx.JCpSimTopMenuMgmt;
import org.jcpsim.jmx.JCpSimTopMenuMgmtMBean;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;

/**
 *
 * @author esteban
 */
public class ScenarioExecutor {
    
    private List<SimulationRuleTemplate> ruleTemplates;
    private JCpSimTopMenuMgmtMBean topMenuMbean;
    private JCpSimPollingClient jcpSimClient;
    private JCpSimPollingClient jcpSimAuxClient;
    
    private int currentSimulationTime = 0;
    private EntityManager em;
    private final String simulationId;

    public ScenarioExecutor(String simulationId, List<SimulationRuleTemplate> ruleTemplates) {
        this.simulationId = simulationId;
        this.ruleTemplates = ruleTemplates;
    }

    public void run(String simulationTime, boolean pauseOnModification) throws Exception{
        this.run(simulationTime, pauseOnModification, Collections.EMPTY_MAP);
    }
    
    public void run(String simulationTime, boolean pauseOnModification, Map<JCpSimParameter, Double> initialParameterValues) throws Exception{
        
        SimulationTemplateEngine engine = new SimulationTemplateEngine(simulationTime);

        for (SimulationRuleTemplate simulationRuleTemplate : ruleTemplates) {
            engine.addRuleTemplate(simulationRuleTemplate);
        }

        initializeControllRules(engine, pauseOnModification);
        
        System.out.println("\n\n\n");
        System.out.println(engine.createSimulationDRL());
        System.out.println("\n\n\n");
        
        final SimulationExecutor executor = new SimulationExecutor(engine, new SimulationExecutor.SimulationListener() {

            public void onStep(int time) {
                currentSimulationTime = time;
            }

            public void onException(Exception e) {
                Logger.getLogger(ScenarioExecutor.class.getName()).log(Level.SEVERE, null, e);
            }

            public void onPause() {
            }

            public void onTermination() {
            }
        });
        
        //copy initial parameter values
        for (Map.Entry<JCpSimParameter, Double> entry : initialParameterValues.entrySet()) {
            executor.addInitialParameterValue(entry.getKey(), entry.getValue());
        }
        
        configureMBeans();

        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("SimulatorPU");
        //em = emf.createEntityManager();
        
        try{
          //  em.getTransaction().begin();
            topMenuMbean.simulationStarted(simulationId);
            executor.executeJMX();
            topMenuMbean.simulationStopped(simulationId);
          //  em.getTransaction().commit();
        }finally{
          //  em.close();
          //  emf.close();
        }
    }
    
    private void takeSnapshot() throws Exception{
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
        jcpSimClient.resume();
    }
    
    private void initializeControllRules(SimulationTemplateEngine engine, boolean pauseOnModification) {
        
        
        
        if (pauseOnModification){
            FreeFormRuleTemplate pauseOnMidificationRule = new FreeFormRuleTemplate();

            StringBuilder pauseOnMidificationRuleBuilder = new StringBuilder();
            pauseOnMidificationRuleBuilder.append("rule \"Pause when value is modified\"\n");
            pauseOnMidificationRuleBuilder.append("    when ValueModifiedToken()\n");
            pauseOnMidificationRuleBuilder.append("then\n");
            pauseOnMidificationRuleBuilder.append("    jCPSimClient.requestPause();\n");
            pauseOnMidificationRuleBuilder.append("end\n");

            pauseOnMidificationRule.setRules(pauseOnMidificationRuleBuilder.toString());
            
            engine.addRuleTemplate(pauseOnMidificationRule);
        }
        
        FreeFormRuleTemplate logOnMidificationRule = new FreeFormRuleTemplate();

        StringBuilder logOnMidificationRuleBuilder = new StringBuilder();
        logOnMidificationRuleBuilder.append("rule \"Log when value is modified\"\n");
        logOnMidificationRuleBuilder.append("    when $v: ValueModifiedToken()\n");
        logOnMidificationRuleBuilder.append("then\n");
        logOnMidificationRuleBuilder.append("    System.out.println(\"\t[\"+System.currentTimeMillis()+\"]-->\"+$v.getTarget().name()+\" modified from \"+$v.getOldValue()+\" to \"+$v.getNewValue());\n");
        logOnMidificationRuleBuilder.append("end\n");

        logOnMidificationRule.setRules(logOnMidificationRuleBuilder.toString());
        
        engine.addRuleTemplate(logOnMidificationRule);
        
        
        FreeFormRuleTemplate logDataRule = new FreeFormRuleTemplate();

        StringBuilder logDataRuleBuilder = new StringBuilder();
        logDataRuleBuilder.append("rule \"Log Data\"\n");
        logDataRuleBuilder.append("    when $d: JCpSimData()\n");
        logDataRuleBuilder.append("then\n");
        logDataRuleBuilder.append("    System.out.println(\"[DATA] \"+$d);");
        logDataRuleBuilder.append("end\n");

        //logDataRule.setRules(logDataRuleBuilder.toString());

        engine.addRuleTemplate(logDataRule);
        
        
    }
    
    private void configureMBeans() throws Exception {
        
        //TopMenu
        JMXServiceURL serviceUrl = new JMXServiceURL(TopMenu.JMX_URL);
        JMXConnector jmxc = JMXConnectorFactory.connect(serviceUrl, null);

        MBeanServerConnection connection = jmxc.getMBeanServerConnection();

        connection.addNotificationListener(new ObjectName(JCpSimTopMenuMgmt.OBJECT_NAME), new NotificationListener() {

            @Override
            public void handleNotification(Notification notification, Object handback) {
                try {
                    takeSnapshot();
                } catch (Exception ex) {
                    Logger.getLogger(ScenarioExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, null, null);
        
        topMenuMbean = JMX.newMBeanProxy(connection, new ObjectName(JCpSimTopMenuMgmt.OBJECT_NAME),
                                                         JCpSimTopMenuMgmtMBean.class, true);
        
        //Main JCpSim
        jcpSimClient = new JCpSimPollingClient(Global.MODE.SIM.getJMXUrl());
        
        //Aux JCpSim
        try{
            jcpSimAuxClient = new JCpSimPollingClient(Global.MODE.AUX.getJMXUrl(), JCpSimCustomRespiratorMgmt.OBJECT_NAME+"_"+Global.MODE.AUX);
        } catch (Exception e){
            
        }
    }

    
}
