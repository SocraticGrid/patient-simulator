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
package com.cognitive.nsf.patientsimulator.jcpsim;

import java.io.IOException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import org.jcpsim.data.JCpSimDataProvider;
import org.jcpsim.jmx.client.JCpSimPollingClient;


public class JMXJCpSimDataProviderFactory implements JCpSimDataProviderFactory {

    private final String jmxURL;
    private final String jmxObject;

    public JMXJCpSimDataProviderFactory(String jmxURL, String jmxObject) {
        this.jmxURL = jmxURL;
        this.jmxObject = jmxObject;
    }
    
    @Override
    public JCpSimDataProvider createProvider() {
        try {
            return new JCpSimPollingClient(jmxURL, jmxObject);
        } catch (IOException | MalformedObjectNameException | InstanceNotFoundException ex) {
            throw new IllegalStateException("Error creating JCpSim Client", ex);
        }
    }
    
}
