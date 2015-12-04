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
package com.cognitive.simulator;

import com.cognitive.template.FreeFormRuleTemplate;
import com.cognitive.template.PeriodicalFixedRuleTemplate;
import com.cognitive.template.PeriodicalRuleTemplate;
import com.cognitive.template.PointInTimeRuleTemplate;
import com.cognitive.template.SimpleRuleTemplate;
import com.cognitive.template.SimulationTemplateEngine;
import com.cognitive.template.ThresholdRuleTemplate;
import org.jcpsim.data.JCpSimParameter;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class SimulatorTemplateTest {

    @Test
    public void testTemplate() throws Exception {

        SimulationTemplateEngine engine = new SimulationTemplateEngine("60s");

        engine.addRuleTemplate(new PeriodicalFixedRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, 0.1));
        engine.addRuleTemplate(new PeriodicalFixedRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, 0.1));
        engine.addRuleTemplate(new PointInTimeRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, 0.1));
        engine.addRuleTemplate(new PeriodicalRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, new double[]{0.1, -0.1}));
        engine.addRuleTemplate(new ThresholdRuleTemplate("data[V_PEEP] < 10", JCpSimParameter.P_COMPLIANCE, -0.0012, 10000));
        engine.addRuleTemplate(new ThresholdRuleTemplate("data[V_PEEP] >= 10", JCpSimParameter.P_COMPLIANCE, 0.0012,10000));

        //engine.addRuleTemplate(new ThresholdRuleTemplate("data[JCpSimParameter.O_PLUNG] > 35", JCpSimParameter.P_COMPLIANCE, 0.006, 10000, "data[JCpSimParameter.O_PLUNG] <= 35"));

        
        FreeFormRuleTemplate freeFormRuleTemplate = new FreeFormRuleTemplate();
        freeFormRuleTemplate.addImport("import java.util.List;");
        freeFormRuleTemplate.addImport("java.util.Date;");
        
        freeFormRuleTemplate.setRules("rule 'test r'\nwhen\nthen\nend");
        
        engine.addRuleTemplate(freeFormRuleTemplate);
        
        
        String whenPart = "\t$data: JCpSimData($peep: data[JCpSimParameter.V_PEEP])";
        String thenPart = "\tdouble $value = 27 + (($peep - 10)*-1.6);\n";
        thenPart+= "\tinsert(new ChangeValueToken($data, JCpSimParameter.P_SHUNT, $value, drools.getRule().getName() ));\n";
        
        engine.addRuleTemplate(new SimpleRuleTemplate(whenPart, thenPart));
        
        System.out.println("\n\n\n------------------------\n");
        System.out.println(engine.getRuleTemplates().get(0).getRules());
        System.out.println("\n\n\n");
        
        
    }
}
