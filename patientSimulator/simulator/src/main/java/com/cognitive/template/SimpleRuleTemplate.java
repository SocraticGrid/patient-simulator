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
