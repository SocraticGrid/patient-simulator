/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
