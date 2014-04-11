/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.data.disease;

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
public class ARDSDiseaseSimulationRules implements DiseaseSimulationRules{

    private String periodicalRulesRate = "1m";
    private long thresholdRulesThreshold = 6000;

    public ARDSDiseaseSimulationRules() {
    }
    
    
    public ARDSDiseaseSimulationRules(String periodicalRulesRate, long thresholdRulesThreshold) {
        this.periodicalRulesRate = periodicalRulesRate;
        this.thresholdRulesThreshold = thresholdRulesThreshold;
    }
    
    public List<SimulationRuleTemplate> getRules() {
        List<SimulationRuleTemplate> rules = new ArrayList<SimulationRuleTemplate>();
        
        /***************************/
        /* ARDS Changes Over Time  */
        /***************************/
        
        //Compliance manipulation
        PeriodicalRuleTemplate complianceManipulationRule = 
                new PeriodicalRuleTemplate(
                    "ARDS_complianceManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0, -0.005, -0.005, -0.005, -0.005, -0.005, -0.0025, -0.0025, 0.0025, 0.0025,  0.005, 0.005,  0.005,  0.005, 0.005},
                    JCpSimParameter.P_COMPLIANCE);
        complianceManipulationRule.setSalience(-1);
        
        //Resistence Pressure manipulation
        PeriodicalRuleTemplate resistenceManipulationRule = 
                new PeriodicalRuleTemplate(
                    "ARDS_resistenceManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0, 1, 2, 2, 2, 2, 2, 2, -2, -2, -2, -2, -2, -2, -1},
                    JCpSimParameter.P_RESISTANCE);
        resistenceManipulationRule.setSalience(-1);
        
        
        //Opening Pressure manipulation
        PeriodicalRuleTemplate openingPressureManipulationRule = 
                new PeriodicalRuleTemplate(
                    "ARDS_openingPressureManipulationRule",
                    periodicalRulesRate,                     
                    new double[]{0, 2, 2, 2, 2, 1, 1, 1, -1, -1, -1, -2, -2, -2, -2},
                    JCpSimParameter.P_OPENING_PRESSURE);
        openingPressureManipulationRule.setSalience(-1);
        
        
        //Shunt manipulation
        PeriodicalRuleTemplate shuntManipulationRule = 
                new PeriodicalRuleTemplate(
                    "ARDS_shuntManipulationRule",
                    periodicalRulesRate,                     
                        new double[]{0, 5, 5, 5, 5, 5, 4, 3, -3, -4, -5, -5, -5, -5, -5},
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
                "2 * ($op - $peep)", //increase shunt
                thresholdRulesThreshold); 
        
        
        //PEEPl > OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplGraterOpeningPressureChangeShunt = new ThresholdRuleTemplate(
                "ARDS_peeplGraterOPChangeShunt",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] > data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_SHUNT, 
                "1 * ($op - $peep)",   //reduce shunt 
                thresholdRulesThreshold);
        
        
        
        /****************************************/
        /* Ventilator Management of Compliance  */
        /****************************************/
        
        //PEEPl < OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplLessOpeningPressureChangeCompliance = new ThresholdRuleTemplate(
                "ARDS_peeplLessOP",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] < data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_COMPLIANCE, 
                "-0.00012 * ($op - $peep)", 
                thresholdRulesThreshold); 
        
        
        //PEEP > OPENING_PRESSURE for more than 1 minute
        ThresholdRuleTemplate peeplGraterOpeningPressureChangeCompliance = new ThresholdRuleTemplate(
                "ARDS_peeplGraterOP",
                "$peep: data[JCpSimParameter.O_PEEP_L], $op: data[JCpSimParameter.P_OPENING_PRESSURE], data[JCpSimParameter.O_PEEP_L] > data[JCpSimParameter.P_OPENING_PRESSURE]", 
                JCpSimParameter.P_COMPLIANCE, 
                "0.00012 * ($peep - $op)", 
                thresholdRulesThreshold); 
        

        
        /****************************************/
        /* Ventilator Management & Lung injury  */
        /****************************************/
        
        //If PLUNG > 35 for more than 1 minute: barotrauma
        ThresholdRuleTemplate plungGrater35 = new ThresholdRuleTemplate(
                "ARDS_plungGrater35",
                "data[JCpSimParameter.O_G_PLUNG] > 35", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.00006, 
                thresholdRulesThreshold); 
        
        
        //If O_G_TV_WEIGHT > 6 for more than 1 minute: volutrauma
        ThresholdRuleTemplate tvWeightGrater6 = new ThresholdRuleTemplate(
                "ARDS_tvWeightGrater6",
                "data[JCpSimParameter.O_G_TV_WEIGHT] > 6", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.00012, 
                thresholdRulesThreshold); 
        
        //If O_G_TV_WEIGHT < 4 for more than 1 minute: shear stress
        ThresholdRuleTemplate tvWeightLess4 = new ThresholdRuleTemplate(
                "ARDS_tvWeightLess4",
                "data[JCpSimParameter.O_G_TV_WEIGHT] < 4", 
                JCpSimParameter.P_COMPLIANCE, 
                -0.0006, 
                thresholdRulesThreshold); 
        
        
        rules.add(complianceManipulationRule);
        rules.add(openingPressureManipulationRule);
        rules.add(resistenceManipulationRule);
        rules.add(shuntManipulationRule);
        rules.add(peeplGraterOpeningPressureChangeShunt);
        rules.add(peeplLessOpeningPressureChangeShunt);
        rules.add(peeplGraterOpeningPressureChangeCompliance);
        rules.add(peeplLessOpeningPressureChangeCompliance);
        //rules.add(plungGrater35);
        //rules.add(tvWeightGrater6);
        //rules.add(tvWeightLess4);
        
        
        return rules;
    }
    
}
