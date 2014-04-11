/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.template;

import java.util.ArrayList;
import java.util.List;


public class FreeFormRuleTemplate implements SimulationRuleTemplate {

    private String globalInitCode;
    private String rules;
    
    private List<String> imports = new ArrayList<String>();
    private List<String> globals = new ArrayList<String>();
    private List<String> initialInserts = new ArrayList<String>();

    public FreeFormRuleTemplate() {
    }

    public FreeFormRuleTemplate(String globalInitCode, String rules) {
        this.globalInitCode = globalInitCode;
        this.rules = rules;
    }

    public FreeFormRuleTemplate(String rules) {
        this.rules = rules;
    }
    
    public String getGlobalInitCode() {
        return this.globalInitCode;
    }

    public String getGlobals() {
        return this.convertToString("global ", globals);
    }

    public String getInitialInserts() {
        return this.convertToString(null, initialInserts);
    }

    public String getRules() {
        return this.rules;
    }

    public String getImports() {
        return this.convertToString("import ", imports);
    }

    public void setGlobalInitCode(String globalInitCode) {
        this.globalInitCode = globalInitCode;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
    
    public void addImport(String importLine){
        this.imports.add(importLine);
    }
    
    public void addGlobal(String globalLine){
        this.globals.add(globalLine);
    }
    
    public void addInitialInsert(String insertLine){
        this.initialInserts.add(insertLine);
    }
    
    private String convertToString(String prefix, List<String> data){
        StringBuilder string = new StringBuilder();
        
        for (String d : data) {
            if (prefix != null && !d.startsWith(prefix)){
                string.append(prefix);
            }
            string.append(d);
            string.append("\n");
        }
        
        
        return string.toString();
    }
    
}
