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
 * Modifies a JCpSim PARAMETER based on a predefined set of fixed values.
 * The set of values acts as an infinite set where the tail of the set is 
 * linked to the head of it.
 * The modification rate is dictated by {@link #timeExpression}.
 * The changes are ALWAYS RELATIVE to the current value of the parameter.
 * For example, a {@link #values} of [-1, 1] and a {@link #timeExpression} of "1s" 
 * will decrease by 1 the current value of the JCpSim PARAMETER specified by
 * {@link #target} in the first second, then it will increase the value by 1
 * in the second second, and then the loop begins again: -1, +1, -1, +1, -1, etc.
 * @author esteban
 */
public class PeriodicalRuleTemplate implements SimulationRuleTemplate {

    private String id = "id"+UUID.randomUUID().toString().replaceAll("\\-", "");
    private String timeExpression;
    private String initialDelayExpression = "0s";
    private JCpSimParameter target;
    private Double[] values;
    private int salience = 0;

    protected PeriodicalRuleTemplate() {
    }

    public PeriodicalRuleTemplate(String id, String timeExpression, double[] values, JCpSimParameter target) {
        this(timeExpression, target, values);
        this.id = id;
        
    }
    
    public PeriodicalRuleTemplate(String timeExpression, JCpSimParameter target, double[] values) {
        this.timeExpression = timeExpression;
        this.target = target;
        
        this.values = new Double[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = values[i];
        }
        
    }
    
    public PeriodicalRuleTemplate(String initialDelayExpression, String timeExpression, JCpSimParameter target, double[] values) {
        this(timeExpression, target, values);
        this.initialDelayExpression = initialDelayExpression;
    }

    @Override
    public String getGlobals() {
        return "global Deque "+id+"Values;";
    }

    @Override
    public String getGlobalInitCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("Values = new ArrayDeque();\n");
        for (double v : values) {
            sb.append(id).append("Values.addLast(").append(v).append(");\n");
        }
        sb.append("kcontext.getKnowledgeRuntime().setGlobal(\"").append(id).append("Values\",").append(id).append("Values);\n");
        return sb.toString();
    }

    @Override
    public String getRules() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nrule \"Periodical ").append(id).append("\"\n");
        sb.append("no-loop true\n");
        sb.append("timer (int:").append(this.initialDelayExpression).append(" ").append(this.timeExpression).append(")\n");
        sb.append("when\n");
        sb.append("        //TODO: add a custom object instead of a plain String\n");
        sb.append("        String(this == \"").append(this.id).append("\")\n");
        sb.append("        not SuspendExecutionToken()\n");
        sb.append("then\n");
        sb.append("        double $value = (Double)").append(id).append("Values.poll();\n");
        sb.append("        ").append(id).append("Values.addLast($value);\n");
        sb.append("        insert(new ChangeValueToken(JCpSimParameter.").append(this.target.name()).append(", String.valueOf($value), drools.getRule().getName(),").append(this.salience).append("));\n");
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

    public Double[] getValues() {
        return values;
    }

    public void setValues(Double[] values) {
        this.values = values;
    }
    
    public String getImports() {
        return null;
    }

    public String getInitialDelayExpression() {
        return initialDelayExpression;
    }

    public int getSalience() {
        return salience;
    }

    public void setSalience(int salience) {
        this.salience = salience;
    }
    
}
