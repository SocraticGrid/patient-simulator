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
 * Applies a periodical, fixed-value change every {@link #timeExpression} on a
 * JCpSim PARAMETER.
 * The changes are ALWAYS RELATIVE to the current value of the parameter.
 * For example, a {@link #value} of -1 and a {@link #timeExpression} of "1s" 
 * will decrease by 1 the current value of the JCpSim PARAMETER specified by
 * {@link #target} every second.
 * @author esteban
 */
public class PeriodicalFixedRuleTemplate implements SimulationRuleTemplate {

    private String id = "id"+UUID.randomUUID().toString().replaceAll("\\-", "");
    private String timeExpression;
    private String initialDelayExpression = "0s";
    private JCpSimParameter target;
    private double value;

    protected PeriodicalFixedRuleTemplate() {
    }
    
    public PeriodicalFixedRuleTemplate(String id, String timeExpression,  double value, JCpSimParameter target) {
        this(timeExpression, target, value);
        this.id = id;
    }
    
    public PeriodicalFixedRuleTemplate(String timeExpression, JCpSimParameter target, double value) {
        this.timeExpression = timeExpression;
        this.target = target;
        this.value = value;
    }
    
    public PeriodicalFixedRuleTemplate(String initialDelayExpression, String timeExpression, JCpSimParameter target, double value) {
        this(timeExpression, target, value);
        this.initialDelayExpression = initialDelayExpression;
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
        sb.append("\nrule \"PeriodicalFixed ").append(id).append("\"\n");
        sb.append("no-loop true\n");
        sb.append("timer (int:").append(this.initialDelayExpression).append(" ").append(this.timeExpression).append(")\n");
        sb.append("when\n");
        sb.append("        //TODO: add a custom object instead of a plain String\n");
        sb.append("        String(this == \"").append(this.id).append("\")\n");
        sb.append("        not SuspendExecutionToken()\n");
        sb.append("then\n");
        sb.append("        double $value = ").append(this.value).append(";\n");
        sb.append("        insert(new ChangeValueToken(JCpSimParameter.").append(this.target.name()).append(", String.valueOf($value), drools.getRule().getName()));\n");
        sb.append("end\n\n");
        return sb.toString();
        
    }

    @Override
    public String getInitialInserts() {
        return "insert(\""+this.id+"\");";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeExpression() {
        return timeExpression;
    }

    public void setTimeExpression(String timeExpression) {
        this.timeExpression = timeExpression;
    }

    public JCpSimParameter getTarget() {
        return target;
    }

    public void setTarget(JCpSimParameter target) {
        this.target = target;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getImports() {
        return null;
    }

    public String getInitialDelayExpression() {
        return initialDelayExpression;
    }
    
    
}
