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
public class ARDSScenarioExecutorTest {
    
    @Test
    public void doTest() throws Exception{
        
        String simulationId = UUID.randomUUID().toString();
        boolean pauseOnModification = false;

        //Time configuration
        String simulationTotalTime = "10m";
        String periodicalRulesRate = "10s";
        long thresholdRulesThreshold = 6000;
        
        
        //Initial parameters
        Map<JCpSimParameter, Double> initialParameterValues = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
        initialParameterValues.put(JCpSimParameter.P_WEIGHT, 75.0);
        initialParameterValues.put(JCpSimParameter.P_OPENING_PRESSURE, 7.0);
        initialParameterValues.put(JCpSimParameter.P_SHUNT, 15.0);
        
        List<SimulationRuleTemplate> rules = new ARDSDiseaseSimulationRules(periodicalRulesRate, thresholdRulesThreshold).getRules();
        
        ScenarioExecutor executor = new ScenarioExecutor(simulationId, rules);
        
        long t1 = System.nanoTime();
        executor.run(simulationTotalTime, pauseOnModification, initialParameterValues);
        System.out.println("Total= "+(System.nanoTime() - t1)/1000000000);
    }
    
}
