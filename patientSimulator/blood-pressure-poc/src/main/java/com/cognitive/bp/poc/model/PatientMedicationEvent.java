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
package com.cognitive.bp.poc.model;

/**
 *
 * @author esteban
 */
public class PatientMedicationEvent {
    private String patientId;
    private long timestamp;
    private String drugLabel;
    private String drugClass;
    private String dose;
    private String route;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDrugLabel() {
        return drugLabel;
    }

    public void setDrugLabel(String drugLabel) {
        this.drugLabel = drugLabel;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDrugClass() {
        return drugClass;
    }

    public void setDrugClass(String drugClass) {
        this.drugClass = drugClass;
    }

    @Override
    public String toString() {
        return "PatientMedicationEvent{" + "patientId=" + patientId + ", timestamp=" + timestamp + ", drugLabel=" + drugLabel + ", drugClass=" + drugClass + ", dose=" + dose + ", route=" + route + '}';
    }
    
    
}
