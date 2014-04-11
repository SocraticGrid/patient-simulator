/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
@Entity
public class JCpSimDataSample implements Serializable{

    @Id
    private String id;
    
    private String simulationId;
    
    private long simulationTime;
    
    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyClass(JCpSimParameter.class)
    @CollectionTable(name="Parameters", joinColumns={
        @JoinColumn(name="sampleId")
    })
    private Map<JCpSimParameter, Double> params = new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class);
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date sampleDate;

    @OneToMany(cascade= CascadeType.ALL)
    private List<Expectation> expectations;

    protected JCpSimDataSample() {
    }

    public JCpSimDataSample(String simulationId){
        id = UUID.randomUUID().toString();
        sampleDate = new Date();
        this.simulationId = simulationId;
    }
    
    public static JCpSimDataSample fromJCpSimData(String simulationId, JCpSimData data){
        JCpSimDataSample obj = new JCpSimDataSample(simulationId);

        for (JCpSimParameter parameter : JCpSimParameter.values()) {
            obj.set(parameter, data.get(parameter));
        }
        
        obj.fillMissingInformation();
        
        return obj;
    }
    
    public String getId() {
        return id;
    }

    public Date getSampleDate() {
        return sampleDate;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(long simulationTime) {
        this.simulationTime = simulationTime;
    }

    public String getSimulationId() {
        return simulationId;
    }
    
    public void fillMissingInformation(){
        for (JCpSimParameter parameter : JCpSimParameter.values()) {
            if (!this.params.containsKey(parameter)){
                this.params.put(parameter, 0.0);
            }
        }
    }
    
    public void set(JCpSimParameter parameter, double value){
        this.params.put(parameter, value);
    }
    
    public double get(JCpSimParameter parameter){
        Double result = this.get(parameter);
        return result==null?0.0:result;
    }

    public Map<JCpSimParameter, Double> getParams() {
        return params;
    }

    public void setParams(Map<JCpSimParameter, Double> params) {
        this.params = params;
    }

    public List<Expectation> getExpectations() {
        return expectations;
    }

    public void setExpectations(List<Expectation> expectations) {
        this.expectations = expectations;
    }
    
}
