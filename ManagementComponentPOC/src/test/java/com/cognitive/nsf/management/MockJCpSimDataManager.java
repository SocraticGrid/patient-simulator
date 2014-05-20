/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management;

import com.cognitive.nsf.management.jcpsim.JCpSimDataUtils;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;
import org.jcpsim.data.JCpSimDataManager;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class MockJCpSimDataManager implements JCpSimDataManager{

    private JCpSimData data = new JCpSimDataImpl();

    public MockJCpSimDataManager() {
        //Default values
        
        data.set(JCpSimParameter.O_G_GAS_CALCULATED, 0.0);
        data.set(JCpSimParameter.O_G_LUNG_VOLUME , 0.87);
        data.set(JCpSimParameter.O_G_PLUNG , 21.7);
        data.set(JCpSimParameter.O_G_PRESP , 21.7);
        data.set(JCpSimParameter.O_G_TIDAL_VOLUME , 0.67);
        data.set(JCpSimParameter.O_G_TV_WEIGHT , 9.5);
        
        data.set(JCpSimParameter.O_FLOW, 0.0);
        data.set(JCpSimParameter.O_PH, 7.36);
        data.set(JCpSimParameter.O_PCO2, 41.0);
        data.set(JCpSimParameter.O_PO2, 71.0);
        
        data.set(JCpSimParameter.V_FIO2, 30.0);
        data.set(JCpSimParameter.V_FLOW_RATE, 0.49);
        data.set(JCpSimParameter.V_FREQUENCY, 12.0);
        data.set(JCpSimParameter.V_INSPIRATORY_TIME, 40.0);
        data.set(JCpSimParameter.V_PAUSE_TIME, 10.0);
        data.set(JCpSimParameter.V_PEEP, 5.0);
        data.set(JCpSimParameter.V_PIP, 23.5);
        
        data.set(JCpSimParameter.P_BAR_PRESSURE, 760.0);
        data.set(JCpSimParameter.P_CARDIAC_OUTPUT, 5.0);
        data.set(JCpSimParameter.P_COMPLIANCE, 0.04);
        data.set(JCpSimParameter.P_HGB, 15.0);
        data.set(JCpSimParameter.P_OPENING_PRESSURE, 5.0);
        data.set(JCpSimParameter.P_RESISTANCE, 10.0);
        data.set(JCpSimParameter.P_SHUNT, 35.0);
        data.set(JCpSimParameter.P_TEMPERATURE, 37.0);
        data.set(JCpSimParameter.P_VCO2, 200.0);
        data.set(JCpSimParameter.P_VO2, 250.0);
        data.set(JCpSimParameter.P_WEIGHT, 70.0);
    }
    
    public void requestPause() {
    }

    public void resume() {
    }

    public synchronized JCpSimData getData() {
        return JCpSimDataUtils.cloneJCpSimData(data);
    }

    public synchronized void set(JCpSimParameter parameter, double value) {
        data.set(parameter, value);
    }

    public String getDataAsString() {
        return "MOCK";
    }
    
}
