/*
 * JCpSimMgmt.java
 *
 * Created on September 5, 2012, 3:53 PM
 */
package org.jcpsim.jmx;

import java.util.Map;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.scenarios.CustomRespirator;

/**
 * Class JCpSimMgmt
 *
 * @author esteban
 */
public class JCpSimCustomRespiratorMgmt implements JCpSimCustomRespiratorMgmtMBean {
    
    public static final String OBJECT_NAME =  "org.jcpsim:type=CustomRespirator";
    
    private final CustomRespirator respirator;
    
    public JCpSimCustomRespiratorMgmt(CustomRespirator respirator) {
        this.respirator = respirator;
    }

    public JCpSimData getData() {
        JCpSimDataImpl data = new JCpSimDataImpl();
        
        //Ventilator data
        data.set(JCpSimParameter.V_PEEP, this.respirator.getvPEEP());
        data.set(JCpSimParameter.V_PIP, this.respirator.getvPIP());
        data.set(JCpSimParameter.V_INSPIRATORY_TIME, this.respirator.getvInspiratoryTime());
        data.set(JCpSimParameter.V_PAUSE_TIME, this.respirator.getvPauseTime());
        data.set(JCpSimParameter.V_FREQUENCY, this.respirator.getvFrequency());
        data.set(JCpSimParameter.V_FIO2, this.respirator.getvFIO2());
        data.set(JCpSimParameter.V_FLOW_RATE, this.respirator.getvFlowRate());
        
        //Patient data
        data.set(JCpSimParameter.P_COMPLIANCE, this.respirator.getpCompliance());
        data.set(JCpSimParameter.P_RESISTANCE, this.respirator.getpResistance());
        data.set(JCpSimParameter.P_SHUNT, this.respirator.getpShunt());
        data.set(JCpSimParameter.P_DEADSPACE, this.respirator.getpDeadSpace());
        data.set(JCpSimParameter.P_BAR_PRESSURE, this.respirator.getpBarPressure());
        data.set(JCpSimParameter.P_CARDIAC_OUTPUT, this.respirator.getpCardiacOutput());
        data.set(JCpSimParameter.P_HGB, this.respirator.getpHGB());
        data.set(JCpSimParameter.P_TEMPERATURE, this.respirator.getpTemperature());
        data.set(JCpSimParameter.P_VCO2, this.respirator.getpVCO2());
        data.set(JCpSimParameter.P_VO2, this.respirator.getpVO2());
        data.set(JCpSimParameter.P_WEIGHT, this.respirator.getpWeight());
        data.set(JCpSimParameter.P_OPENING_PRESSURE, this.respirator.getpOpeningPressure());
        
        //Output data
        data.set(JCpSimParameter.O_FLOW, this.respirator.getoFlow());
        data.set(JCpSimParameter.O_LUNG_VOLUME, this.respirator.getoLungVolume());
        data.set(JCpSimParameter.O_PCO2, this.respirator.getoPCO2());
        data.set(JCpSimParameter.O_PH, this.respirator.getoPH());
        data.set(JCpSimParameter.O_PLUNG, this.respirator.getoPlung());
        data.set(JCpSimParameter.O_PO2, this.respirator.getoPO2());
        data.set(JCpSimParameter.O_PRESP, this.respirator.getoPresp());
        data.set(JCpSimParameter.O_R, this.respirator.getoR());
        data.set(JCpSimParameter.O_TIDAL_VOLUME, this.respirator.getoTidalVolume());
        data.set(JCpSimParameter.O_TV_WEIGHT, this.respirator.getoTV_Weight());
        data.set(JCpSimParameter.O_PEEP_L, this.respirator.getoPEEPl());
        data.set(JCpSimParameter.O_AutoPEEP, this.respirator.getoAutoPEEP());
        data.set(JCpSimParameter.O_VQ, this.respirator.getoVQ());
        data.set(JCpSimParameter.O_AADO2, this.respirator.getoAADO2());
        
        //Output data GAS
        data.set(JCpSimParameter.O_G_LUNG_VOLUME, this.respirator.getOgLungVolume());
        data.set(JCpSimParameter.O_G_PLUNG, this.respirator.getOgPlung());
        data.set(JCpSimParameter.O_G_PRESP, this.respirator.getOgPresp());
        data.set(JCpSimParameter.O_G_TIDAL_VOLUME, this.respirator.getOgTidalVolume());
        data.set(JCpSimParameter.O_G_TV_WEIGHT, this.respirator.getOgTV_Weight());
        data.set(JCpSimParameter.O_G_GAS_CALCULATED, this.respirator.getOgGasCalculated());
        
        
        
        //time
        data.setTime(this.respirator.getTime());
        
        
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
            //Ventilator
            case V_FIO2:
                this.respirator.setvFIO2(value);
                break;
            case V_FLOW_RATE:
                this.respirator.setvFlowRate(value);
                break;
            case V_FREQUENCY:
                this.respirator.setvFrequency(value);
                break;
            case V_INSPIRATORY_TIME:
                this.respirator.setvInspiratoryTime(value);
                break;
            case V_PAUSE_TIME:
                this.respirator.setvPauseTime(value);
                break;
            case V_PEEP:
                this.respirator.setvPEEP(value);
                break;
            case V_PIP:
                this.respirator.setvPIP(value);
                break;
            
            //Patient
            case P_BAR_PRESSURE:
                this.respirator.setpBarPressure(value);
                break;
            case P_CARDIAC_OUTPUT:
                this.respirator.setpCardiacOutput(value);
                break;
            case P_COMPLIANCE:
                this.respirator.setpCompliance(value);
                break;
            case P_HGB:
                this.respirator.setpHGB(value);
                break;
            case P_RESISTANCE:
                this.respirator.setpResistance(value);
                break;
            case P_SHUNT:
                this.respirator.setpShunt(value);
                break;
            case P_DEADSPACE:
                this.respirator.setpDeadSpace(value);
                break;
            case P_TEMPERATURE:
                this.respirator.setpTemperature(value);
                break;
            case P_VCO2:
                this.respirator.setpVCO2(value);
                break;
            case P_VO2:
                this.respirator.setpVO2(value);
                break;
            case P_WEIGHT:
                this.respirator.setpWeight(value);
                break;
            case P_OPENING_PRESSURE:
                this.respirator.setpOpeningPressure(value);
                break;
            
            // GAS Calculated Output
            case O_G_GAS_CALCULATED:
                this.respirator.setOgGasCalculated(value);
                break;
            
            default:
                throw new IllegalArgumentException(parameter+" is READ-ONLY!");
        }
        
    }

    public void requestPause() {
        this.respirator.requestPause();
    }

    public void resume() {
        this.respirator.resume();
    }

    
}
