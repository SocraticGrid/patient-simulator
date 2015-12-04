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
package com.cognitive.template;

import java.util.UUID;
import org.jcpsim.data.JCpSimParameter;

/**
 * Applies a relative change to a JCpSim PARAMETER prorated in a time threshold
 * (in milliseconds).
 * For example, a {@link #valueExpression} of "1" and a {@link #threshold} of
 * 60000 will increase the {@link #target} value of a JCpSimData sample by 1
 * after 1 minute. The change will be gradual over that minute and will depend
 * on the sample rate of the simulation. So, a sample rate of 2 samples/minute
 * will generate 2 changes in the {@link #target} value: +0.5 and +0.5 again.
 * 3 samples/minute will create 3 modifications: +0.33, +0.33, +0.33.<br/>
 * Negative {@link #valueExpression} are also supported.<br/>
 * This template also support custom conditions in the JCpSimData sample specified
 * by {@link #condition}.
 * <br/>
 * <br/>
 * <b>IMPORTANT:</b> In order for this template to work properly, the
 * sample rate of the simulation MUST be smaller than the {@link #threshold}.
 * <br/>
 * <br/>
 * For example, the rule "Decrease COMPLIANCE by 0.00006/minute when O_G_PLUNG > 35"
 * could be written as:
 * <br/>
 * <pre>
 * {@code 
 * ThresholdRuleTemplate plungGrater35 = new ThresholdRuleTemplate(
 *               "ARDS_plungGrater35",                      //name of the rule
 *               "data[JCpSimParameter.O_G_PLUNG] > 35",    //condition
 *               JCpSimParameter.P_COMPLIANCE,              //taget
 *               -0.00006,                                  //value
 *               60000);                                    //over this period of time
 * }
 * </pre>
 * <br/>
 * <br/>
 * This template also support the specification of a valid Time Window. A Time
 * Window, if defined, will stablish when the rule is allowed to be fired.<br/>
 * The Time Window of the template is defined by {@link #timeWindow} parameter.
 * If one of the bounds of the Time Window is null, the bound is considered open.
 * The bounds of a Time Window are specified in milliseconds and are relative 
 * <br/>
 * <br/>
 * <b>IMPORTANT:</b> When using a Time Window, there MUST be sample data that
 * corresponds with each of the bounds of the window. So, if the window has the
 * bounds [10s, 30s], there MUST be a JCpSimData sample at time 10s and 30s.
 * <br/>
 * <br/>
 * @author esteban
 */
public class ThresholdRuleTemplate implements SimulationRuleTemplate {

    private String id = "id"+UUID.randomUUID().toString().replaceAll("\\-", "");
    private String condition;
    private JCpSimParameter target;
    private String valueExpression;
    private long threshold;
    private TimeWindow timeWindow;
    
    public static class TimeWindow{
        private final Long from;
        private final Long to;

        public TimeWindow(Long from, Long to) {
            this.from = from;
            this.to = to;
        }

        public Long getFrom() {
            return from;
        }

        public Long getTo() {
            return to;
        }
        
    }

    protected ThresholdRuleTemplate() {
    }

    public ThresholdRuleTemplate(String id, String condition, JCpSimParameter target, double value, long threshold) {
        this(id, condition, target, String.valueOf(value), threshold);
    }
    
    public ThresholdRuleTemplate(String id, String condition, JCpSimParameter target, String valueExpression, long threshold) {
        this(condition, target, valueExpression, threshold);
        this.id = id;
    }
    
    public ThresholdRuleTemplate(String id, String condition, JCpSimParameter target, double value, long threshold, TimeWindow timeWindow) {
        this(id, condition, target, String.valueOf(value), threshold, timeWindow);
    }
    
    public ThresholdRuleTemplate(String id, String condition, JCpSimParameter target, String valueExpression, long threshold, TimeWindow timeWindow) {
        this(condition, target, valueExpression, threshold, timeWindow);
        this.id = id;
    }
    
    public ThresholdRuleTemplate(String condition, JCpSimParameter target, double value, long threshold) {
        this(condition, target, String.valueOf(value), threshold);
    }
    
    public ThresholdRuleTemplate(String condition, JCpSimParameter target, String valueExpression, long threshold) {
        this(condition, target, valueExpression, threshold, null);
    }
    
    public ThresholdRuleTemplate(String condition, JCpSimParameter target, double value, long threshold, TimeWindow timeWindow) {
        this(condition, target, String.valueOf(value), threshold, timeWindow);
    }
    
    public ThresholdRuleTemplate(String condition, JCpSimParameter target, String valueExpression, long threshold, TimeWindow timeWindow) {
        this.condition = condition;
        this.target = target;
        this.valueExpression = valueExpression;
        this.threshold = threshold;
        this.timeWindow = timeWindow;
    }

    @Override
    public String getGlobals() {
        return "";
    }

    @Override
    public String getGlobalInitCode() {
        return "";
    }

    @Override
    public String getRules() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getAffirmativeRule());
        
        return sb.toString();
    }

    private String getAffirmativeRule(){
        StringBuilder sb = new StringBuilder();
        sb.append("\nrule \"Threshold ").append(id).append("\"\n");
        sb.append("no-loop true\n");        
        sb.append("when\n");
	sb.append("        $data: JCpSimData(").append(this.condition).append(")\n");
        sb.append("        not SuspendExecutionToken()\n");
        sb.append("        $clock:SimulationClockToken( associatedTo == $data, $timeDiffMillis: timeDiffMillis");
        if (timeWindow != null){
            if (this.timeWindow.getFrom() != null){
                sb.append(", timeMillis >= ").append(this.timeWindow.getFrom());
            }
            if (this.timeWindow.getTo() != null){
                sb.append(", timeMillis <= ").append(this.timeWindow.getTo());
            }
        }
        sb.append(")\n");
        sb.append("then\n");
	//sb.append("     double $value = (").append(this.valueExpression).append(") * (sampleRate/(double)").append(this.threshold).append(");\n");
	sb.append("     double $value = ((double)").append(this.valueExpression).append(") /(double)").append(this.threshold).append(" * (double)$timeDiffMillis ;\n");
	sb.append("     System.out.println(\"Threashold rule '\"+drools.getRule().getName()+\"' fired at: '\"+ $clock.getTimeMillis()+\" and adding a delta of '\"+$value+\"' to current AA_P_MOD of '\"+$data.get(JCpSimParameter.AA_P_MOD)+\"'\");\n");
	sb.append("     insert(new ChangeValueToken(JCpSimParameter.").append(this.target.name()).append(", String.valueOf($value), drools.getRule().getName()));\n");
        sb.append("end\n");
        return sb.toString();
    }
    
    @Override
    public String getInitialInserts() {
        return "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public JCpSimParameter getTarget() {
        return target;
    }

    public void setTarget(JCpSimParameter target) {
        this.target = target;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String value) {
        this.valueExpression = value;
    }

    public long getThreshold() {
        return threshold;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }
    
    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }

    public String getImports() {
        return null;
    }
}
