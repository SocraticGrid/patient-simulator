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
package org.jcpsim.jmx;

import java.io.Serializable;
import javax.management.Notification;
import org.jcpsim.data.JCpSimData;

/**
 *
 * @author esteban
 */
public class JCpSimDataNotification extends Notification implements Serializable {
    
    private JCpSimData data;
    
    public JCpSimDataNotification(String type, Object source, long sequenceNumber) {
        super(type, source, sequenceNumber);
    }

    public JCpSimData getData() {
        return data;
    }

    public void setData(JCpSimData data) {
        this.data = data;
    }
    
    
}
