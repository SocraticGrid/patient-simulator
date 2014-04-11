/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.template;

import java.util.UUID;
import org.jcpsim.data.JCpSimParameter;

/**
 *
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
        sb.append("        modify($threshold){\n");
        sb.append("            setValue(").append(this.value).append(", kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime())\n");
        sb.append("        }\n");
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