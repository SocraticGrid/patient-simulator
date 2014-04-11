/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.simulator;

import com.cognitive.template.PeriodicalFixedRuleTemplate;
import com.cognitive.template.PeriodicalRuleTemplate;
import com.cognitive.template.PointInTimeRuleTemplate;
import com.cognitive.template.SimulationTemplateEngine;
import com.cognitive.template.ThresholdRuleTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jcpsim.data.JCpSimParameter;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author esteban
 */
public class SerializationTest {

    private SimulationTemplateEngine createEngine() {
        SimulationTemplateEngine engine = new SimulationTemplateEngine("60s");

        engine.addRuleTemplate(new PeriodicalFixedRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, 0.1));
        engine.addRuleTemplate(new PointInTimeRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, 0.1));
        engine.addRuleTemplate(new PeriodicalRuleTemplate("10s", JCpSimParameter.P_COMPLIANCE, new double[]{0.1, -0.1}));
        engine.addRuleTemplate(new ThresholdRuleTemplate("data[V_PEEP] < 10", JCpSimParameter.P_COMPLIANCE, -0.0012, 10000));
        //engine.addRuleTemplate(new ThresholdRuleTemplate("data[V_PEEP] >= 10", JCpSimParameter.P_COMPLIANCE, 0.0012,10000));

        engine.addRuleTemplate(new ThresholdRuleTemplate("data[O_PLUNG] > 35", JCpSimParameter.P_COMPLIANCE, 0.006, 10000));

        return engine;
    }

    @Test
    public void testGson() throws Exception {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String text = gson.toJson(this.createEngine());

        System.out.println("\n\n" + text + "\n\n");

    }

    @Test
    public void testYaml() throws Exception {

        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        String text = yaml.dump(this.createEngine());
        System.out.println("\n\n" + text + "\n\n");

        SimulationTemplateEngine engine = (SimulationTemplateEngine) yaml.load(text);
        
        System.out.println(engine);
        
    }
}
