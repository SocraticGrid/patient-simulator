/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.data;

/**
 *
 * @author esteban
 */
public enum JCpSimParameter {
    //Ventilator
    V_PEEP,
    V_PIP,
    V_INSPIRATORY_TIME,
    V_PAUSE_TIME,
    V_FREQUENCY,
    V_FLOW_RATE,
    V_FIO2,
    
    //Patient
    P_RESISTANCE,
    P_COMPLIANCE,
    P_VO2,
    P_VCO2,
    P_CARDIAC_OUTPUT,
    P_HGB,
    P_TEMPERATURE,
    P_BAR_PRESSURE,
    P_SHUNT,
    P_DEADSPACE,
    P_WEIGHT,
    P_OPENING_PRESSURE,
    
    //Output
    O_PLUNG,
    O_FLOW,
    O_LUNG_VOLUME,
    O_PRESP, 
    O_R,
    O_PH,
    O_TV_WEIGHT,
    O_PEEP_L,
    O_TIDAL_VOLUME,
    O_VQ,
    O_PO2,
    O_PCO2,
    O_MINUTE_VENTILATION,
    O_AADO2,
    O_AutoPEEP,
    
    //Output GAS
    O_G_PLUNG,
    O_G_LUNG_VOLUME,
    O_G_PRESP, 
    O_G_TV_WEIGHT,
    O_G_TIDAL_VOLUME,
    O_G_GAS_CALCULATED,
    
    
    //Time
    TIME
}
