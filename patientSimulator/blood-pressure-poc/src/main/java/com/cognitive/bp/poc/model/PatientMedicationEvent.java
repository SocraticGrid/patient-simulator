/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
