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
