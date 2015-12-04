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
public class PneumoniaDiseaseSimulationRules implements DiseaseSimulationRules{

    private String periodicalRulesRate = "1m";
    private long thresholdRulesThreshold = 60000;

    public PneumoniaDiseaseSimulationRules() {
    }
    
    
    public PneumoniaDiseaseSimulationRules(String periodicalRulesRate, long thresholdRulesThreshold) {
        this.periodicalRulesRate = periodicalRulesRate;
        this.thresholdRulesThreshold = thresholdRulesThreshold;
    }
    
    public List<SimulationRuleTemplate> getRules() {
        List<SimulationRuleTemplate> rules = new ArrayList<SimulationRuleTemplate>();
        
        /********************************/
        /* Pneumonia Changes Over Time  */
        /********************************/
        
        
        //Compliance manipulation
        PeriodicalRuleTemplate complianceManipulationRule = 
                new PeriodicalRuleTemplate(
                    "PNEUMONIA_complianceManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0,-0.0025, -0.0025, -0.0025, -0.0025, -0.0025, -0.0025, -0.0025, 0.0025, 0.0025,  0.0025, 0.0025,  0.0025,  0.0025, 0.0025},
                    JCpSimParameter.P_COMPLIANCE);
        complianceManipulationRule.setSalience(-1);
        
        
        //Opening Pressure manipulation
        PeriodicalRuleTemplate openingPressureManipulationRule = 
                new PeriodicalRuleTemplate(
                    "PNEUMONIA_openingPressureManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5},
                    JCpSimParameter.P_OPENING_PRESSURE);
        openingPressureManipulationRule.setSalience(-1);
        
        //Shunt manipulation
        PeriodicalRuleTemplate shuntManipulationRule = 
                new PeriodicalRuleTemplate(
                    "PNEUMONIA_shuntManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, 1, -1, -1, -1},
                    JCpSimParameter.P_SHUNT);
        shuntManipulationRule.setSalience(-1);
        

        
        /*************************************/
        /* Ventilator Management of % Shunt  */
        /*************************************/
        
        //PEEPl < OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplLessOpeningPressureChangeShunt = new ThresholdRuleTemplate(
                "ARDS_peeplLessOPChangeShunt",
                "$shunt: data[JCpSimParameter.P_SHUNT], $peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] < data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_SHUNT, 
                "2 * ($op - $peep)", 
                thresholdRulesThreshold); 
        
        
        /****************************************/
        /* Ventilator Management of Compliance  */
        /****************************************/
        
        //PEEPl < OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplLessOpeningPressureChangeCompliance = new ThresholdRuleTemplate(
                "PNEUMONIA_peeplLessOP",
                "$shunt: data[JCpSimParameter.P_SHUNT], $peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] < data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_COMPLIANCE, 
                "-0.00012 * ($op - $peep)", 
                thresholdRulesThreshold); 
        
        //OPENING_PRESSURE < PEEPl < OPENING_PRESSURE+2 for more than 1 minute
        ThresholdRuleTemplate peeplBetweenOpeningPressureAndOpeningPressurePlus2ChangeCompliance = new ThresholdRuleTemplate(
                "PNEUMONIA_peeplBetweenOPandOPPlus2",
                "data[JCpSimParameter.O_PEEP_L] >= data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] <= (data[JCpSimParameter.P_OPENING_PRESSURE]+2)", 
                JCpSimParameter.P_COMPLIANCE, 
                0.00006, 
                thresholdRulesThreshold);
        
        
        //PEEPl > OPENING_PRESSURE+2 for more than 1 minute
        ThresholdRuleTemplate peeplGraterOpeningPressurePlus2ChangeCompliance = new ThresholdRuleTemplate(
                "PNEUMONIA_peeplGraterOPPlus2",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] > (data[JCpSimParameter.P_OPENING_PRESSURE]+2)", 
                JCpSimParameter.P_COMPLIANCE, 
                "-1 * Math.abs(0.0012 * ($peep - $op))", 
                thresholdRulesThreshold); 
        
        
        
        /****************************************/
        /* Ventilator Management & Lung injury  */
        /****************************************/
        
        //If PLUNG > 30 for more than 1 minute -> barotrauma
        ThresholdRuleTemplate peepOrPlungGrater30 = new ThresholdRuleTemplate(
                "PNEUMONIA_plungGrater30",
                "data[JCpSimParameter.O_PLUNG] > 30", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.0006, 
                thresholdRulesThreshold); 
        
        
        //If O_TV_WEIGHT > 10 for more than 1 minute -> volutrauma
        ThresholdRuleTemplate tvWeightGrater10 = new ThresholdRuleTemplate(
                "PNEUMONIA_tvWeightGrater10",
                "data[JCpSimParameter.O_TV_WEIGHT] > 10", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.0012, 
                thresholdRulesThreshold); 
        
        //If O_TV_WEIGHT < 8 for more than 1 minute -> shear stress
        ThresholdRuleTemplate tvWeightLess8 = new ThresholdRuleTemplate(
                "PNEUMONIA_tvWeightLess8",
                "data[JCpSimParameter.O_TV_WEIGHT] < 8", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.0006, 
                thresholdRulesThreshold); 
        
        
        
        rules.add(complianceManipulationRule);
        rules.add(openingPressureManipulationRule);
        rules.add(shuntManipulationRule);
        rules.add(peeplLessOpeningPressureChangeShunt);
        rules.add(peeplLessOpeningPressureChangeCompliance);
        rules.add(peeplBetweenOpeningPressureAndOpeningPressurePlus2ChangeCompliance);
        rules.add(peeplGraterOpeningPressurePlus2ChangeCompliance);
        //rules.add(peepOrPlungGrater30);
        //rules.add(tvWeightGrater10);
        //rules.add(tvWeightLess8);
        
        return rules;
    }
    
}
