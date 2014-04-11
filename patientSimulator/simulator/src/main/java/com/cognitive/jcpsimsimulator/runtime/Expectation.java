/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.runtime;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author esteban
 */
@Entity
public class Expectation implements Serializable {
    @Id
    private String id;
    
    private String parameterName;
    private Double parameterValue;

    public Expectation() {
    }

    public Expectation(String id, String parameterName, Double parameterValue) {
        this.id = id;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public Double getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(Double parameterValue) {
        this.parameterValue = parameterValue;
    }

}
