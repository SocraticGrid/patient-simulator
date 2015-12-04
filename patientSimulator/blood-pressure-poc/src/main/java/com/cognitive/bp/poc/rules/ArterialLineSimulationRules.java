/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.rules;

import com.cognitive.bp.poc.model.PatientMedicationEvent;
import com.cognitive.template.FreeFormRuleTemplate;
import com.cognitive.template.SimulationRuleTemplate;
import com.cognitive.template.ThresholdRuleTemplate;
import java.util.ArrayList;
import java.util.List;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ArterialLineSimulationRules {

    public static enum RULE_TYPE {

        WAVE,
        DRUG;
    }

    public static class ArterialLineSimulationRuleConfiguration {

        private final String name;
        private final double delta;
        private final long duration;
        private final RULE_TYPE type;

        public ArterialLineSimulationRuleConfiguration(String name, double delta, long duration, RULE_TYPE type) {
            this.name = name;
            this.delta = delta;
            this.duration = duration;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public double getDelta() {
            return delta;
        }

        public long getDuration() {
            return duration;
        }

        public RULE_TYPE getType() {
            return type;
        }

    }

    private final List<ArterialLineSimulationRuleConfiguration> configurations;
    private final int repetitions;

    public ArterialLineSimulationRules(List<ArterialLineSimulationRuleConfiguration> configurations, int repetitions) {
        this.configurations = configurations;
        this.repetitions = repetitions;
    }

    public List<SimulationRuleTemplate> getRules() {

        List<SimulationRuleTemplate> rules = new ArrayList<>();

        long time = 0L;
        long loopExecutionTime = getLoopExecutionTime();
        
        for (int i = 0; i < this.repetitions; i++) {

            for (ArterialLineSimulationRuleConfiguration config : this.configurations) {

                switch (config.getType()) {
                    case WAVE:
                        ThresholdRuleTemplate rule = new ThresholdRuleTemplate(config.getName() + "_" + i,
                                "",
                                JCpSimParameter.AA_P_MOD,
                                config.getDelta(),
                                config.getDuration(),
                                new ThresholdRuleTemplate.TimeWindow(time, time + config.getDuration()));

                        rules.add(rule);
                        time += config.getDuration();
                        break;
                    case DRUG:
                        rules.add(this.createDrugInsertionRule(config.getName()+ "_" + i, config.getDuration() + (loopExecutionTime * i)));
                        break;
                    default:
                        throw new IllegalArgumentException("Don't know how to process rule of type "+config.getType());
                }

            }

        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\nrule \"Log Clock\"\n");
        sb.append("no-loop true\n");
        sb.append("when\n");
        sb.append("        not SuspendExecutionToken()\n");
        sb.append("        $clock:SimulationClockToken( $timeMillis: timeMillis, $timeDiffMillis: timeDiffMillis)\n");
        sb.append("then\n");
        sb.append("     System.out.println(\"SimulationClockToken: \"+$timeMillis+\", \"+$timeDiffMillis);\n");
        sb.append("end\n");
        rules.add(new FreeFormRuleTemplate(sb.toString()));
        
        return rules;
    }

    public long getTotalExecutionTime() {
        return this.getLoopExecutionTime() * this.repetitions;
    }
    
    public long getLoopExecutionTime() {
        long time = 0L;
        for (ArterialLineSimulationRuleConfiguration config : this.configurations) {
            if (config.getType() == RULE_TYPE.WAVE){
                time += config.getDuration();
            }
        }
        return time;
    }

    private SimulationRuleTemplate createDrugInsertionRule(String id, long time) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nrule \"Drug ").append(id).append("\"\n");
        sb.append("no-loop true\n");
        sb.append("when\n");
        sb.append("        not SuspendExecutionToken()\n");
        sb.append("        $clock:SimulationClockToken( $timeDiffMillis: timeDiffMillis");
        sb.append("             , timeMillis < (").append(time).append(" + $timeDiffMillis)");
        sb.append("             , timeMillis >= ").append(time);
        sb.append("         )\n");
        sb.append("then\n");
        sb.append("     System.out.println(\"Creating Drug Prescription Event at \"+System.currentTimeMillis());\n");
        sb.append("     \n");
        sb.append("     PatientMedicationEvent event = new PatientMedicationEvent();\n");
        sb.append("     event.setDose(\"q6h\");\n");
        sb.append("     event.setDrugClass(\"http://schemes.caregraf.info/rxnorm#866516\");\n");
        sb.append("     event.setDrugLabel(\"Metoprolol Tartrate 50 MG Oral Tablet [Lopressor]\");\n");
        sb.append("     event.setPatientId(\"2\");\n");
        sb.append("     event.setTimestamp(System.currentTimeMillis());\n");
        sb.append("     \n");
        sb.append("     PatientDataService.getInstance().storePatientMedicationsAsynchronoulsy(event);\n");
        sb.append("end\n");
        
        System.out.println(sb.toString());
        
        return new FreeFormRuleTemplate(sb.toString());
    }
}
