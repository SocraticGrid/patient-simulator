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
package com.cognitive.jcpsim;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.client.JCpSimPollingClient;
import org.jcpsim.run.Global;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class JCpSimClientTest {
    
    @Test
    public void doTest() throws Exception{
        String url = Global.MODE.SIM.getJMXUrl();
        final JCpSimPollingClient client = new JCpSimPollingClient(url);
        
        new Thread(new Runnable() {

            private long t = 0;
            private long sleep = 1000;
            
            public void run() {
                while (t < 60000){
                    try {
                        JCpSimData data = client.getData();
                        System.out.println("PEEP= "+data.get(JCpSimParameter.V_PEEP));
                        client.set(JCpSimParameter.V_PIP, data.get(JCpSimParameter.V_PEEP)+1);
                        Thread.sleep(sleep);
                        t+= sleep;
                    } catch (Exception ex) {
                        Logger.getLogger(JCpSimClientTest.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
            }
        }).start();
        
        Thread.currentThread().join();
        
    }
}
