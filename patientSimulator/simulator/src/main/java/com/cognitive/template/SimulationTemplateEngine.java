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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author esteban
 */
public class SimulationTemplateEngine {
    private String id = UUID.randomUUID().toString();
    private String simulationTime; 
    private List<SimulationRuleTemplate> ruleTemplates = new ArrayList<SimulationRuleTemplate>();
    private final transient StringTemplate template;

    public SimulationTemplateEngine() {
        this(null);
    }
    
    public SimulationTemplateEngine(String simulationTime) {
        try {
            this.simulationTime = simulationTime;
            template = new StringTemplate(IOUtils.toString(SimulationTemplateEngine.class.getResourceAsStream("/templates/Scenario.tpl")));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean addRuleTemplate(SimulationRuleTemplate ruleTemplate) {
        return ruleTemplates.add(ruleTemplate);
    }

    public String createSimulationDRL(){
        List<String> imports = new ArrayList<String>();
        List<String> globals = new ArrayList<String>();
        List<String> globalInitCodes = new ArrayList<String>();
        List<String> initialInserts = new ArrayList<String>();
        List<String> rules = new ArrayList<String>();
        
        template.reset();
        
        for (SimulationRuleTemplate simulationRuleTemplate : this.ruleTemplates) {
            imports.add(simulationRuleTemplate.getImports());
            globals.add(simulationRuleTemplate.getGlobals());
            globalInitCodes.add(simulationRuleTemplate.getGlobalInitCode());
            initialInserts.add(simulationRuleTemplate.getInitialInserts());
            rules.add(simulationRuleTemplate.getRules());
        }
        
        template.setAttribute("imports", imports);
        template.setAttribute("globals", globals);
        template.setAttribute("globalInitCodes", globalInitCodes);
        template.setAttribute("initialInserts", initialInserts);
        template.setAttribute("rules", rules);
        
        template.setAttribute("simulationTime", simulationTime);
        return template.toString();
        
    }

    public String getId() {
        return id;
    }

    public String getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(String simulationTime) {
        this.simulationTime = simulationTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SimulationRuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<SimulationRuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }
    
}
