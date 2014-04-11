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
public class SimpleValueModificationRuleTemplate implements SimulationRuleTemplate {

    private String id = "id"+UUID.randomUUID().toString().replaceAll("\\-", "");
    private String whenPart;
    private JCpSimParameter target;
    private String valueExpression;

    protected SimpleValueModificationRuleTemplate() {
    }

    public SimpleValueModificationRuleTemplate(String id, String whenPart, JCpSimParameter target, double value) {
        this(whenPart, target, value);
        this.id = id;
    }
    
    public SimpleValueModificationRuleTemplate(String id, String whenPart, JCpSimParameter target, String valueExpression) {
        this(whenPart, target, valueExpression);
        this.id = id;
    }
    
    public SimpleValueModificationRuleTemplate(String whenPart, JCpSimParameter target, double value) {
        this(whenPart, target, String.valueOf(value));
    }
    
    public SimpleValueModificationRuleTemplate(String whenPart, JCpSimParameter target, String valueExpression) {
        this.whenPart = whenPart;
        this.target = target;
        this.valueExpression = valueExpression;
    }
    

    public String getWhenPart() {
        return whenPart;
    }

    public void setWhenPart(String whenPart) {
        this.whenPart = whenPart;
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

    public void setValue(String valueExpression) {
        this.valueExpression = valueExpression;
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
        sb.append("\nrule \"Simple ").append(id).append("\"\n");
        sb.append("no-loop true\n");
        sb.append("when\n");
        sb.append("        not SuspendExecutionToken()\n");
        sb.append(this.whenPart);
        sb.append("then\n");
        sb.append("        JCpSimData $data = jCPSimClient.getData();\n");
        sb.append("        String $value = \"").append(this.valueExpression).append("\";\n");
        sb.append("        insert(new ChangeValueToken($value, drools.getRule().getName()));\n");
        sb.append("end\n\n");
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

    public String getImports() {
        return null;
    }

    
}
