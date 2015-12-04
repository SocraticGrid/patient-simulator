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
package com.cognitive.vo2calculator;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
//import sun.org.mozilla.javascript.internal.NativeArray;
//import sun.org.mozilla.javascript.internal.NativeObject;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author esteban
 */
public class Vo2Formula {

    /**
     * inputs
     */
    private double fiO2;
    private double hgb;
    private double co; //Cardiac Output
    private double temp;
    private double pb;
    private double vo2;
    private double vco2;
    private String[] vqsIndex = new String[]{"0", "0.05", "0.1", "0.3", "0.5", "1.67", "2.0", "3.0", "6.0", "10.0", "999999"};
    private double[] vqs = new double[11];

    public void setFiO2(double fiO2) {
        this.fiO2 = fiO2;
    }

    public void setHgb(double hgb) {
        this.hgb = hgb;
    }

    public void setCo(double co) {
        this.co = co;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setPb(double pb) {
        this.pb = pb;
    }

    public void setVo2(double vo2) {
        this.vo2 = vo2;
    }

    public void setVco2(double vco2) {
        this.vco2 = vco2;
    }

    public void setVqs(double[] vqs) {
        this.vqs = vqs;
    }

    public void setVqsIndex(String[] vqsIndex) {
        this.vqsIndex = vqsIndex;
    }
    
    public List<Compartment> compute() throws Exception {

        /*
        var txtVO2 = 0;
        var txtVCO2 = 0;
        var txtCO = 0;
        var txtHgb = 0;
        var txtTemp = 0;
        var txtPb = 0;
        var txtFiO2 = 0;
         */
        
        this.setJSVariableValue("txtVO2", this.vo2);
        this.setJSVariableValue("txtVCO2", this.vco2);
        this.setJSVariableValue("txtCO", this.co);
        this.setJSVariableValue("txtHgb", this.hgb);
        this.setJSVariableValue("txtTemp", this.temp);
        this.setJSVariableValue("txtPb", this.pb);
        this.setJSVariableValue("txtFiO2", this.fiO2);

        this.resetJSArrays();
        
        for (int i = 0; i < vqsIndex.length; i++) {
            this.setJSSliderArraysValues(i, Double.valueOf(vqsIndex[i]), vqs[i]);
        }
        
        this.getScriptEngineAsJSInvocable().invokeFunction("compute");
        
        return this.prepareResults();
    }

    protected void setJSVariableValue(String variable, Double value){
        try{
            this.getScriptEngine().eval(variable+" = "+value+";");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    protected void resetJSArrays(){
        
        try{
            
            this.getScriptEngine().eval("results = new Array();");
            
            this.getScriptEngine().eval("SliderArrays = new Array();");
            for (int i = 0; i < vqsIndex.length; i++) {
                this.getScriptEngine().eval("SliderArrays.push(new Array());");
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        

    }
    
    protected void setJSSliderArraysValues(int index, double value1, double value2){
        try{
            this.getScriptEngine().eval("SliderArrays["+index+"].push("+value1+");");
            this.getScriptEngine().eval("SliderArrays["+index+"].push('');");
            this.getScriptEngine().eval("SliderArrays["+index+"].push('');");
            this.getScriptEngine().eval("SliderArrays["+index+"].push("+value2+");");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    private ScriptEngine engine;

    protected ScriptEngine getScriptEngine() throws ScriptException, NoSuchMethodException {
        if (engine == null) {
            ScriptEngineManager manager = new ScriptEngineManager();
            engine = manager.getEngineByName("JavaScript");

            engine.eval(new InputStreamReader(Vo2Formula.class.getResourceAsStream("/js/VQN2.js")));
            engine.eval(new InputStreamReader(Vo2Formula.class.getResourceAsStream("/js/VO2.js")));

        }
        
        return engine;
    }
    
    protected Invocable getScriptEngineAsJSInvocable() throws ScriptException, NoSuchMethodException {
        
        return (Invocable)this.getScriptEngine();
    }

    /**************************************************************************/
    /*                              JDK 7                                     */
    /**************************************************************************/
    /*
    private Compartment convertToCompartment(NativeObject obj) {
        Compartment compartment = new Compartment();

        for (Object object : obj.getIds()) {
            String index = (String) object;
            //System.out.println(object + " -> " + obj.get(index, null));
            compartment.set(index, Double.valueOf(obj.get(index, null).toString()));
        }

        return compartment;
    }

    private List<Compartment> prepareResults() throws ScriptException, NoSuchMethodException {
        List<Compartment> results = new ArrayList<Compartment>();

        NativeArray array = (NativeArray) this.getScriptEngineAsJSInvocable().invokeFunction("getResults");
        
        for (Object id : array.getIds()) {
            Compartment c = this.convertToCompartment((NativeObject)array.get((Integer)id, null));
            results.add(c);
        }
        
        NativeObject obj = (NativeObject) this.getScriptEngineAsJSInvocable().invokeFunction("getAbgResult");
        results.add(this.convertToCompartment(obj));
        return results;
    }
    */
    
    
    /**************************************************************************/
    /*                              JDK 8                                     */
    /**************************************************************************/
    private Compartment convertToCompartment(ScriptObjectMirror obj) {
        Compartment compartment = new Compartment();

        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            compartment.set(entry.getKey(), Double.valueOf(entry.getValue().toString()));
            
        }
        
        return compartment;
    }
    
    private List<Compartment> prepareResults() throws ScriptException, NoSuchMethodException {
        List<Compartment> results = new ArrayList<Compartment>();

        
        
        ScriptObjectMirror getResults = (ScriptObjectMirror) this.getScriptEngineAsJSInvocable().invokeFunction("getResults");
        for (Object value : getResults.values()) {
            Compartment c = this.convertToCompartment((ScriptObjectMirror)value);
            results.add(c);
        }
        
        ScriptObjectMirror getAbgResult = (ScriptObjectMirror)this.getScriptEngineAsJSInvocable().invokeFunction("getAbgResult");
        results.add(this.convertToCompartment(getAbgResult));
        
        
        return results;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vo2Formula{" + "fiO2=").append(fiO2).append(", hgb=").append(hgb).append(", co=").append(co).append(", temp=").append(temp).append(", pb=").append(pb).append(", vo2=").append(vo2).append(", vco2=").append(vco2).append(", vqsIndex=[");
        for (String vqi : vqsIndex) {
            builder.append(vqi).append(",");
        }
        builder.append("], vqs=[");
        for (double d : vqs) {
            builder.append(d).append(",");
        }
        builder.append("], engine=").append(engine).append('}');
        return builder.toString();
    }

    protected double getFiO2() {
        return fiO2;
    }

    protected double getHgb() {
        return hgb;
    }

    protected double getCo() {
        return co;
    }

    protected double getTemp() {
        return temp;
    }

    protected double getPb() {
        return pb;
    }

    protected double getVo2() {
        return vo2;
    }

    protected double getVco2() {
        return vco2;
    }

    protected String[] getVqsIndex() {
        return vqsIndex;
    }

    protected double[] getVqs() {
        return vqs;
    }

    protected ScriptEngine getEngine() {
        return engine;
    }

    
}
