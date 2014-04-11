/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.template.serialization;

import com.cognitive.template.SimulationTemplateEngine;
import java.io.InputStream;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author esteban
 */
public class SimulationTemplateEngineSerializer {
 
    private final static SimulationTemplateEngineSerializer INSTANCE = new SimulationTemplateEngineSerializer();

    
    
    private SimulationTemplateEngineSerializer() {
    }
    
    public static SimulationTemplateEngineSerializer getInstance(){
        return INSTANCE;
    }
    
    public String serialize(SimulationTemplateEngine engine, boolean pretty){
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(pretty);
        Yaml yaml = new Yaml(options);
        
        return yaml.dump(engine);
        
    }
    
    public SimulationTemplateEngine deserialize(String data){
        Yaml yaml = new Yaml();
        return (SimulationTemplateEngine) yaml.load(data);
    }
}
