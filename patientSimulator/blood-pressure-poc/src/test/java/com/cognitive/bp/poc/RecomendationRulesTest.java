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
package com.cognitive.bp.poc;

import com.cognitive.bp.poc.model.PatientMedicationEvent;
import com.cognitive.bp.poc.model.PeakJCpSimData;
import com.cognitive.bp.poc.model.RecommendationChange;
import com.cognitive.bp.poc.model.gnome.SNP;
import com.cognitive.bp.poc.recommendation.RecommendationSystem;
import com.cognitive.bp.poc.recommendation.RecommendationSystemFactory;
import com.cognitive.bp.poc.recommendation.gnome.GenomeDataService;
import com.cognitive.bp.poc.recommendation.patient.PatientDataService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.jcpsim.data.JCpSimParameter;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class RecomendationRulesTest {

    @Test
    public void doTestWithoutGenomicData() throws Exception {
        this.doTest(true, false);
    }

    @Test
    public void doTestWithGenomicData() throws Exception {
        this.doTest(true, true);
    }

    public void doTest(boolean sendAlerts, boolean useGenomicData) throws InterruptedException, ExecutionException {
        RecommendationSystem rs = new RecommendationSystemFactory()
                .setDebugEnabled(false)
                .build();

        List<Double> values = Arrays.asList(
                9.0,
                13.0,
                13.0,
                10.0,
                11.0,
                // new bucket
                11.0,
                4.0,
                5.0,
                10.0,
                4.0,
                // new bucket
                10.0,
                11.0,
                10.0,
                9.0,
                10.0,
                // new bucket
                09.0,
                10.0,
                13.0,
                10.0,
                11.0
        );

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

        if (sendAlerts) {
            rs.changeParameter(RecommendationChange.PARAMETER.AA_DELAY, "0");
            rs.enableAlerts();
        } else {
            rs.disableAlerts();
        }

        if (useGenomicData) {
            
            SNP snp = GenomeDataService.getInstance().getSNPByRsid("2", "rs1801252");
            if (snp == null){
                GenomeDataService.getInstance().storeSNP("2", new SNP("rs1801252", 114044277, "CC", "10"));
            }
            snp = GenomeDataService.getInstance().getSNPByRsid("2", "rs1801253");
            if (snp == null){
                GenomeDataService.getInstance().storeSNP("2", new SNP("rs1801253", 114045297, "CC", "10"));
            }
            
            rs.enableGenomicData();
        } else {
            rs.disableGenomicData();
        }

        long t = System.currentTimeMillis();
        for (Double v : values) {
            Map<JCpSimParameter, Double> params = new HashMap<>();
            params.put(JCpSimParameter.AA_O_PRESP, v);
            params.put(JCpSimParameter.TIME, t + 1000D);

            rs.notifyPeak(new PeakJCpSimData(params));
        }

        Thread.sleep(1000);
        System.out.println("Done");
    }

    @Test
    public void testAlertStatus() {

        RecommendationSystem rs = new RecommendationSystemFactory()
                .setDebugEnabled(false)
                .build();

        //by default, alerts are disabled;
        Assert.assertFalse(rs.areAlertsActive());

        //let's activate them
        rs.enableAlerts();
        Assert.assertTrue(rs.areAlertsActive());

        //let's disable them
        rs.disableAlerts();
        Assert.assertFalse(rs.areAlertsActive());

    }

    @Test
    public void testSparqlEndpoint() throws ExecutionException {
        GenomeDataService ds = GenomeDataService.getInstance();
        ds.getSNPByRsid("2", "rs1799853");
    }
}
