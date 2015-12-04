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
package com.cognitive.nsf.management.disease;

import com.cognitive.template.PeriodicalRuleTemplate;
import com.cognitive.template.SimulationRuleTemplate;
import com.cognitive.template.ThresholdRuleTemplate;
import java.util.ArrayList;
import java.util.List;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class AsthmaDiseaseSimulationRules implements DiseaseSimulationRules{

    private String periodicalRulesRate = "1m";
    private long thresholdRulesThreshold = 60000;

    public AsthmaDiseaseSimulationRules() {
    }
    
    
    public AsthmaDiseaseSimulationRules(String periodicalRulesRate, long thresholdRulesThreshold) {
        this.periodicalRulesRate = periodicalRulesRate;
        this.thresholdRulesThreshold = thresholdRulesThreshold;
    }
    
    public List<SimulationRuleTemplate> getRules() {
        List<SimulationRuleTemplate> rules = new ArrayList<SimulationRuleTemplate>();
        
        /********************************/
        /* Asthma Changes Over Time  */
        /********************************/
        
        //Resistence Pressure manipulation
        PeriodicalRuleTemplate resistenceManipulationRule = 
                new PeriodicalRuleTemplate(
                    "ASTHMA_resistenceManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0, 5, 10, 10, 10, -10, -10, -10, -5},
                    JCpSimParameter.P_RESISTANCE);
        resistenceManipulationRule.setSalience(-1);
        
        //Opening Pressure manipulation
        PeriodicalRuleTemplate openingPressureManipulationRule = 
                new PeriodicalRuleTemplate(
                    "ASTHMA_openingPressureManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0,1, 0, 1, 1, -1, -1, 0, -1},
                    JCpSimParameter.P_OPENING_PRESSURE);
        openingPressureManipulationRule.setSalience(-1);
        
        //Shunt manipulation
        PeriodicalRuleTemplate shuntManipulationRule = 
                new PeriodicalRuleTemplate(
                    "ASTHMA_shuntManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0, 1, 3, 4, 3, -3, -4, -3, -1},
                    JCpSimParameter.P_SHUNT);
        shuntManipulationRule.setSalience(-1);
        

        
        /*************************************/
        /* Ventilator Management of % Shunt  */
        /*************************************/
        
        //PEEPl < OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplLessOpeningPressureChangeShunt = new ThresholdRuleTemplate(
                "ARDS_peeplLessOPChangeShunt",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] < data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_SHUNT, 
                "1 * ($op - $peep)", //The shunt will increase because $op > $peep
                thresholdRulesThreshold); 
        
        
        //PEEP > OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplGraterOpeningPressureChangeShunt = new ThresholdRuleTemplate(
                "ARDS_peeplGraterOPChangeShunt",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] > data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_SHUNT, 
                "1 * ($op - $peep)", //The shunt will decrease because $op < $peep
                thresholdRulesThreshold);
        
        /****************************************/
        /* Ventilator Management of Compliance  */
        /****************************************/
        
        //PEEPl < OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplLessOpeningPressureChangeCompliance = new ThresholdRuleTemplate(
                "ASTHMA_peeplLessOP",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] < data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_COMPLIANCE, 
                "-0.00012 * ($op - $peep)", 
                thresholdRulesThreshold); 
        
        //PEEPl > OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplGraterOpeningPressureChangeCompliance = new ThresholdRuleTemplate(
                "ASTHMA_peeplGraterOP",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] > data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_COMPLIANCE, 
                "0.00012 * ($peep - $op)", 
                thresholdRulesThreshold); 
        
        
        
        /****************************************/
        /* Ventilator Management & Lung injury  */
        /****************************************/
        
        //If PLUNG > 25 for more than 1 minute -> barotrauma
        ThresholdRuleTemplate peepOrPlungGrater25 = new ThresholdRuleTemplate(
                "ASTHMA_plungGrater25",
                "data[JCpSimParameter.O_PLUNG] > 25", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.0006, 
                thresholdRulesThreshold); 
        
        
        //If O_TV_WEIGHT > 8 for more than 1 minute -> volutrauma
        ThresholdRuleTemplate tvWeightGrater8 = new ThresholdRuleTemplate(
                "ASTHMA_tvWeightGrater8",
                "data[JCpSimParameter.O_TV_WEIGHT] > 8", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.0012, 
                thresholdRulesThreshold); 
        
        //If O_TV_WEIGHT < 6 for more than 1 minute -> shear stress
        ThresholdRuleTemplate tvWeightLess6 = new ThresholdRuleTemplate(
                "ASTHMA_tvWeightLess6",
                "data[JCpSimParameter.O_TV_WEIGHT] < 6", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.0006, 
                thresholdRulesThreshold); 
        
        
        
        rules.add(resistenceManipulationRule);
        rules.add(openingPressureManipulationRule);
        rules.add(shuntManipulationRule);
        rules.add(peeplGraterOpeningPressureChangeShunt);
        rules.add(peeplLessOpeningPressureChangeShunt);
        rules.add(peeplLessOpeningPressureChangeCompliance);
        rules.add(peeplGraterOpeningPressureChangeCompliance);
        //rules.add(peepOrPlungGrater25);
        //rules.add(tvWeightGrater8);
        //rules.add(tvWeightLess6);
        
        return rules;
    }
    
}
