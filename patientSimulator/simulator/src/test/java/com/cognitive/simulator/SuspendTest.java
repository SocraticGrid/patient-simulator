/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.simulator;

import com.cognitive.SimulationExecutor;
import com.cognitive.template.PeriodicalFixedRuleTemplate;
import com.cognitive.template.SimulationTemplateEngine;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class SuspendTest {
    
    @Test
    public void doTest() throws Exception{
        
        //Time configuration
        String periodicalRulesRate = "1s";
        
        
        PeriodicalFixedRuleTemplate complianceManipulationRule = new PeriodicalFixedRuleTemplate("ARDS_complianceManipulationRule",
                    periodicalRulesRate,                     
                    0.01,
                    JCpSimParameter.P_COMPLIANCE);
        
        
        SimulationTemplateEngine engine = new SimulationTemplateEngine();
        engine.addRuleTemplate(complianceManipulationRule);
        
        final SimulationExecutor executor = new SimulationExecutor(engine, new SimulationExecutor.SimulationListener() {

            public void onStep(int time) {
            }

            public void onException(Exception e) {
            }

            public void onPause() {
            }
        });
        executor.setDebug(true);
        
        
        final JCpSimPollingClient jcpSimClient = new JCpSimPollingClient(Global.MODE.SIM.getJMXUrl());

        ScheduledExecutorService diseaseThreadExecutor = Executors.newScheduledThreadPool(1);
        diseaseThreadExecutor.schedule(new Runnable() {
            public void run() {
                
                executor.execute(jcpSimClient);
            }
        }, 0, TimeUnit.MILLISECONDS);    
        

        Thread.sleep(5000);
        
        executor.suspend();
        
        Thread.sleep(15000);
        executor.unsuspend();
        
        Thread.sleep(7000);
        
        
    }
    
    
}
