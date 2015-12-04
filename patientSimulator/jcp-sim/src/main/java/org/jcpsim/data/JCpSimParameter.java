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
    
    
    //ARTERIAL_LINE PATIENT
    AA_P_WAVE,
    AA_P_FLUSH,
    AA_P_DAMP,
    AA_P_RLINE,
    AA_P_CLINE,
    AA_P_LLINE,
    AA_P_MOD,

    //ARTERIAL_LINE OUTPUT
    AA_O_FLOW,
    AA_O_PRESP,
    AA_O_FREQ,
    AA_O_DAMP_COEFF,
    AA_O_PREAL,
    
    //Time
    TIME
}
