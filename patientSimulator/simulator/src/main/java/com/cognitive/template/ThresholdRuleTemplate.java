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
public class ThresholdRuleTemplate implements SimulationRuleTemplate {

    private String id = "id"+UUID.randomUUID().toString().replaceAll("\\-", "");
    private String condition;
    private JCpSimParameter target;
    private String valueExpression;
    private long threshold;

    protected ThresholdRuleTemplate() {
    }

    public ThresholdRuleTemplate(String id, String condition, JCpSimParameter target, double value, long threshold) {
        this(id, condition, target, String.valueOf(value), threshold);
    }
    
    public ThresholdRuleTemplate(String id, String condition, JCpSimParameter target, String valueExpression, long threshold) {
        this(condition, target, valueExpression, threshold);
        this.id = id;
    }
    
    public ThresholdRuleTemplate(String condition, JCpSimParameter target, double value, long threshold) {
        this(condition, target, String.valueOf(value), threshold);
    }
    
    public ThresholdRuleTemplate(String condition, JCpSimParameter target, String valueExpression, long threshold) {
        this.condition = condition;
        this.target = target;
        this.valueExpression = valueExpression;
        this.threshold = threshold;
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
//        StringBuilder sb = new StringBuilder();
//        sb.append("\nrule \"Threshold ").append(id).append("\"\n");
//        sb.append("no-loop true\n");
//        sb.append("when\n");
//        sb.append("        $data: JCpSimData(").append(this.condition).append(")\n");
//        sb.append("        $threshold : Threshold(target == JCpSimParameter.").append(target.name()).append(", source == \"").append(id).append("\")\n");
//        sb.append("then\n");
//        sb.append("        double $value = ").append(this.valueExpression).append(";\n");
//        sb.append("        modify($threshold){\n");
//        sb.append("            setValue($value, kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime())\n");
//        sb.append("        }\n");
//        sb.append("end\n\n");
//        return sb.toString();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\nrule \"Threshold ").append(id).append("\"\n");
        sb.append("no-loop true\n");        
        sb.append("when\n");
	sb.append("        $data: JCpSimData(").append(this.condition).append(")\n");
        sb.append("        not SuspendExecutionToken()\n");
        sb.append("then\n");
	sb.append("     double $value = (").append(this.valueExpression).append(") * (sampleRate/(double)").append(this.threshold).append(");\n");
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

    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }

    public String getImports() {
        return null;
    }
}
