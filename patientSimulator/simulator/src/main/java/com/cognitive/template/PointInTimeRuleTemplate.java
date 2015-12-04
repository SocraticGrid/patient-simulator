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
 * One-shot modification of a JCPSim PARAMETER on a specified time.
 * @author esteban
 */
public class PointInTimeRuleTemplate implements SimulationRuleTemplate {

    private String id = "id"+UUID.randomUUID().toString().replaceAll("\\-", "");
    private String timeExpression;
    private JCpSimParameter target;
    private double value;

    protected PointInTimeRuleTemplate() {
    }
    
    public PointInTimeRuleTemplate(String id, String timeExpression, JCpSimParameter target, double value) {
        this(timeExpression, target, value);
        this.id = id;
    }
    
    public PointInTimeRuleTemplate(String timeExpression, JCpSimParameter target, double value) {
        this.timeExpression = timeExpression;
        this.target = target;
        this.value = value;
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
        sb.append("\nrule \"PointInTime ").append(id).append("\"\n");
        sb.append("no-loop true\n");
        sb.append("timer (int:").append(this.timeExpression).append(" 0s)\n");
        sb.append("when\n");
        sb.append("        $threshold : Threshold(target == JCpSimParameter.").append(target.name()).append(", source == \"").append(id).append("\")\n");
        sb.append("then\n");
        sb.append("        double $value = ").append(value).append(";\n");
        sb.append("        insert(new ChangeValueToken(JCpSimParameter.").append(this.target.name()).append(", String.valueOf($value), drools.getRule().getName()));\n");
        sb.append("end\n\n");
        return sb.toString();
    }

    @Override
    public String getInitialInserts() {
        return "insert(new Threshold(\""+this.id+"\", JCpSimParameter."+target.name()+", kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime(), 0));";
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
}
