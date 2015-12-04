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
