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

import java.util.Map;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.scenarios.ArterialLine;

/**
 * Class JCpSimMgmt
 *
 * @author esteban
 */
public class JCpSimArterialLineMgmt extends NotificationBroadcasterSupport implements JCpSimArterialLineMgmtMBean {
    
    public static final String OBJECT_NAME =  "org.jcpsim:type=ArterialLine";
    
    private final ArterialLine arterialLine;
    
    public JCpSimArterialLineMgmt(ArterialLine arterialLine) {
        this.arterialLine = arterialLine;
        
        this.arterialLine.addEventListener(new ArterialLine.ArterialLineEventListener() {

            public void onDataCalculated(JCpSimData data) {
                JCpSimDataNotification notification = new JCpSimDataNotification("JCpSimData", "JCpSimArterialLineMgmt", data.getTime());
                notification.setData(data);
                sendNotification(notification);
            }
        });
    }

    public JCpSimData getData() {
        JCpSimDataImpl data = new JCpSimDataImpl();
        
        //Patient data
        data.set(JCpSimParameter.AA_P_WAVE, this.arterialLine.getAAPWAVE());
        data.set(JCpSimParameter.AA_P_FLUSH, this.arterialLine.getAAPFLUSH());
        data.set(JCpSimParameter.AA_P_DAMP, this.arterialLine.getAAPDAMP());
        data.set(JCpSimParameter.AA_P_RLINE, this.arterialLine.getAAPRLINE());
        data.set(JCpSimParameter.AA_P_CLINE, this.arterialLine.getAAPCLINE());
        data.set(JCpSimParameter.AA_P_LLINE, this.arterialLine.getAAPLLINE());
        data.set(JCpSimParameter.AA_P_MOD, this.arterialLine.getAAPMOD());
        
        //Output data
        data.set(JCpSimParameter.AA_O_FLOW, this.arterialLine.getAAOFLOW());
        data.set(JCpSimParameter.AA_O_PRESP, this.arterialLine.getAAOPRESP());
        data.set(JCpSimParameter.AA_O_FREQ, this.arterialLine.getAAOFREQ());
        data.set(JCpSimParameter.AA_O_DAMP_COEFF, this.arterialLine.getAAODAMPCOEFF());
        data.set(JCpSimParameter.AA_O_PREAL, this.arterialLine.getAAOPREAL());
        
        //time
        data.setTime(this.arterialLine.getTime());
        
        
        return data;
    }
    
    public String getDataAsString() {
        StringBuilder sb = new StringBuilder();
        JCpSimData data = this.getData();
        for (Map.Entry<JCpSimParameter, Double> entry : data.getData().entrySet()) {
            sb.append(entry.getKey().toString());
            sb.append(" -> ");
            sb.append(String.valueOf(entry.getValue()));
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public void set(JCpSimParameter parameter, double value) {
        switch(parameter){
            //Patient
            case AA_P_WAVE:
                this.arterialLine.setAAPWAVE(value);
                break;
            case AA_P_FLUSH:
                this.arterialLine.setAAPFLUSH(value);
                break;
            case AA_P_DAMP:
                this.arterialLine.setAAPDAMP(value);
                break;
            case AA_P_RLINE:
                this.arterialLine.setAAPRLINE(value);
                break;
            case AA_P_CLINE:
                this.arterialLine.setAAPCLINE(value);
                break;
            case AA_P_LLINE:
                this.arterialLine.setAAPLLINE(value);
                break;
            case AA_P_MOD:
                this.arterialLine.setAAPMOD(value);
                break;
            default:
                throw new IllegalArgumentException(parameter+" is READ-ONLY!");
        }
        
    }

    public void requestPause() {
        this.arterialLine.requestPause();
    }

    public void resume() {
        this.arterialLine.resume();
    }

    
}
