/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitive.bp.poc;

import com.cognitive.bp.poc.model.PatientMedicationEvent;
import com.cognitive.bp.poc.recommendation.patient.PatientDataService;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class PatientDataServiceServiceTest {
    
    @Test
    public void doTest() throws IOException{
        
        long time = System.currentTimeMillis();
        String patientId = "2";
        String drugClass = "http://schemes.caregraf.info/rxnorm#866516";
        String drugLabel = "Metoprolol Tartrate 50 MG Oral Tablet [Lopressor]";
        String dose = "q6h";
        
        
        PatientMedicationEvent event = new PatientMedicationEvent();
        event.setDose(dose);
        event.setDrugClass(drugClass);
        event.setDrugLabel(drugLabel);
        event.setPatientId(patientId);
        event.setTimestamp(time);
        
        PatientDataService.getInstance().storePatientMedications(event);
        
        List<PatientMedicationEvent> patientMedications = PatientDataService.getInstance().getPatientMedications("2", time-1000, time+1000);
        Assert.assertEquals(1, patientMedications.size());
        
        Assert.assertEquals(dose, patientMedications.get(0).getDose());
        Assert.assertEquals("rxnorm:866516", patientMedications.get(0).getDrugClass());
        Assert.assertEquals(drugLabel, patientMedications.get(0).getDrugLabel());
        Assert.assertEquals(patientId, patientMedications.get(0).getPatientId());
        //milliseconds get lost in fuseki!
        Assert.assertEquals(Long.valueOf(String.valueOf(time).substring(0, String.valueOf(time).length()-3)+"000"), (Long)patientMedications.get(0).getTimestamp());
        
    }
}
