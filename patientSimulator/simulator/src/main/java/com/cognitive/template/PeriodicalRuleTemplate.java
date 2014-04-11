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
