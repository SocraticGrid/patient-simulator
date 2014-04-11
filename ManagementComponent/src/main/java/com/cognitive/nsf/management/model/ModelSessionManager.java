/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import com.cognitive.nsf.management.jcpsim.JCpSimDataReceivedEventListener;
import com.cognitive.nsf.management.jcpsim.data.JCpSimRecommendedData;
import com.cognitive.nsf.management.model.expectation.ModelExpectationEvaluator;
import com.cognitive.nsf.management.model.expectation.PolicyEnforcer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataWriter;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class ModelSessionManager implements JCpSimDataReceivedEventListener {
    
    private final JCpSimDataWriter dataWriter;
    private final PolicyEnforcer policyEnforcer;
    
    private Set<Model> models = new CopyOnWriteArraySet<Model>(); 
    private Map<Model, ModelExpectationEvaluator> modelExpectationEvaluators = new ConcurrentHashMap<Model, ModelExpectationEvaluator>();
    private Model activeModel;
    private ExecutorService modelExecutor = Executors.newFixedThreadPool(5);

    public ModelSessionManager(JCpSimDataWriter dataWriter, PolicyEnforcer policyEnforcer) {
        this.dataWriter = dataWriter;
        
        policyEnforcer.configure(this);
        this.policyEnforcer = policyEnforcer;
    }
    
    /**
     * Notifies the models and Expectation Evaluators about incoming data from 
     * JCpSim.
     * @param data 
     */
    public void onDataReceived(final JCpSimData data) {
        for (final Model model : models) {
            modelExecutor.execute(new Runnable() {
                public void run() {
                    //notify the model
                    model.processData(data);
                    
                    //notify the model's expectation evaluator
                    ModelExpectationEvaluator expectationEvaluator = modelExpectationEvaluators.get(model);
                    if (expectationEvaluator != null){
                        expectationEvaluator.processData(data);
                    }
                }
            });
            
        }
    }
    
    /**
     * This method must be invoked by the different models to notify the availability
     * of a result.
     * If the notifying model is the active one the result is sent to JCpSim
     * to modify its parameters.
     * @param source 
     * @param result 
     */
    public void onResult(Model source, JCpSimData result){
        
        //Notify the expectation evaluator of the model to check the validity
        //of the recommendation
//        ModelExpectationEvaluator expectationEvaluator = modelExpectationEvaluators.get(source);
//        if (expectationEvaluator != null){
//            expectationEvaluator.processData(JCpSimRecommendedData.fromJCpSimData(result));
//        }
        
        //if this is the active model, copy the output values
        //to JCpSim. If not, discard it.
        if (source == activeModel){
            for (JCpSimParameter jCpSimParameter : JCpSimParameter.values()) {
                if (jCpSimParameter != JCpSimParameter.TIME && !jCpSimParameter.name().startsWith("O_")){
                    dataWriter.set(jCpSimParameter, result.get(jCpSimParameter));
                }
            }

        }
    }
    
    
    
    public void setActiveModel(Model model){
        if (!models.contains(model)){
            throw new IllegalArgumentException("Requested model doesn't exist");
        }
        this.activeModel = model;
    }
    
    public void addModelExpectationEvaluator(ModelExpectationEvaluator modelExpectationEvaluator){
        modelExpectationEvaluator.configure(policyEnforcer);
        this.modelExpectationEvaluators.put(modelExpectationEvaluator.getModel(), modelExpectationEvaluator);
    }
    
    public void addModel(Model model){
        this.addModel(model, false);
    }
    
    public void addModel(Model model, boolean active){
        this.models.add(model);
        model.init(this);
        if (active){
            this.activeModel = model;
        }
    }

}
