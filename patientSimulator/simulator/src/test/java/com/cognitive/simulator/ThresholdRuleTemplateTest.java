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
package com.cognitive.simulator;

import com.cognitive.SimulationExecutor;
import com.cognitive.template.PeriodicalFixedRuleTemplate;
import com.cognitive.template.SimulationTemplateEngine;
import com.cognitive.template.ThresholdRuleTemplate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.JCpSimArterialLineMgmt;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class ThresholdRuleTemplateTest {
    
    @Test
    public void testThresholdRuleTemplate() throws Exception {
        
        
        ThresholdRuleTemplate rule1 = new ThresholdRuleTemplate("RULE1",
                    "",
                    JCpSimParameter.AA_P_RLINE,
                    -1,
                    60000,
                    new ThresholdRuleTemplate.TimeWindow(0L, 60000L));
        
//        ThresholdRuleTemplate rule2 = new ThresholdRuleTemplate("RULE2",
//                    "",
//                    JCpSimParameter.AA_P_RLINE,
//                    2.5,
//                    10000,
//                    new ThresholdRuleTemplate.TimeWindow(60000L, 70000L));
//        
//        ThresholdRuleTemplate rule3 = new ThresholdRuleTemplate("RULE3",
//                    "",
//                    JCpSimParameter.AA_P_RLINE,
//                    -1.5,
//                    10000,
//                    new ThresholdRuleTemplate.TimeWindow(70000L, 80000L));
//        
//        
        SimulationTemplateEngine engine = new SimulationTemplateEngine();
        engine.addRuleTemplate(rule1);
//        engine.addRuleTemplate(rule2);
//        engine.addRuleTemplate(rule3);
        
        final SimulationExecutor executor = new SimulationExecutor(engine, new SimulationExecutor.SimulationListener() {

            public void onStep(int time) {
                System.out.println("TIME= "+time);
            }

            public void onException(Exception e) {
            }

            public void onPause() {
            }

            public void onTermination() {
            }
        }, 1000);
        
        executor.setDebug(false);
        
        
        final JCpSimPollingClient jcpSimClient = new JCpSimPollingClient(Global.MODE.SIM.getJMXUrl(), JCpSimArterialLineMgmt.OBJECT_NAME+"_"+Global.MODE.SIM);

        JCpSimData originalData = jcpSimClient.getData();
        
        ScheduledExecutorService simThreadExecutor = Executors.newScheduledThreadPool(1);
        simThreadExecutor.schedule(new Runnable() {
            public void run() {
                
                executor.execute(jcpSimClient);
            }
        }, 0, TimeUnit.MILLISECONDS);    
        

        Thread.sleep(60000);
        
        JCpSimData finalData = jcpSimClient.getData();
        
        System.out.println("Final delta: "+(originalData.get(JCpSimParameter.AA_P_RLINE) - finalData.get(JCpSimParameter.AA_P_RLINE)));
        Assert.assertEquals(originalData.get(JCpSimParameter.AA_P_RLINE) - finalData.get(JCpSimParameter.AA_P_RLINE), 1, 0.1);
        
    }
    
}
