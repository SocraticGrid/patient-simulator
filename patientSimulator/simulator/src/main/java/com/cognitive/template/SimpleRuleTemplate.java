/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.template;

import java.util.UUID;

/**
 *
 * @author esteban
 */
public class SimpleRuleTemplate implements SimulationRuleTemplate {

    private String id = "id"+UUID.randomUUID().toString().replaceAll("\\-", "");
    private String whenPart;
    private String thenPart;

    protected SimpleRuleTemplate() {
    }

    public SimpleRuleTemplate(String id, String whenPart, String thenPart) {
        this(whenPart, thenPart);
        this.id = id;
    }
    
    public SimpleRuleTemplate(String whenPart, String thenPart) {
        this.whenPart = whenPart;
        this.thenPart = thenPart;
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
        sb.append("\nthen\n");
        sb.append(this.thenPart);
        sb.append("\nend\n\n");
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

    public String getWhenPart() {
        return whenPart;
    }

    public void setWhenPart(String whenPart) {
        this.whenPart = whenPart;
    }

    public String getThenPart() {
        return thenPart;
    }

    public void setThenPart(String thenPart) {
        this.thenPart = thenPart;
    }

}
