/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.disease;

import com.cognitive.nsf.management.executor.ScenarioExecutor;
import com.cognitive.template.SimulationRuleTemplate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jcpsim.data.JCpSimParameter;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class AsthmaScenarioExecutorTest {
    
    @Test
    public void doTest() throws Exception{
        
        String simulationId = UUID.randomUUID().toString();
        boolean pauseOnModification = false;

        //Time configuration
        String simulationTotalTime = "1800s";
        String periodicalRulesRate = "1m";
        long thresholdRulesThreshold = 6000;
        
        //Initial parameters
        Map<JCpSimParameter, Double> initialParameterValues = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
        initialParameterValues.put(JCpSimParameter.P_WEIGHT, 75.0);
        initialParameterValues.put(JCpSimParameter.P_OPENING_PRESSURE, 4.0);
        initialParameterValues.put(JCpSimParameter.P_SHUNT, 10.0);
        initialParameterValues.put(JCpSimParameter.P_RESISTANCE, 10.0);
        
        
        List<SimulationRuleTemplate> rules = new AsthmaDiseaseSimulationRules(periodicalRulesRate, thresholdRulesThreshold).getRules();
        
        ScenarioExecutor executor = new ScenarioExecutor(simulationId, rules);
        
        long t1 = System.nanoTime();
        executor.run(simulationTotalTime, pauseOnModification, initialParameterValues);
        System.out.println("Total= "+(System.nanoTime() - t1)/1000000000);
    }
    
}
