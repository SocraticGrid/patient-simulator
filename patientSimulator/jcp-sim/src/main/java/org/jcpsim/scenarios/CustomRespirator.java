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
package org.jcpsim.scenarios;

import com.cognitive.vo2calculator.CachedVo2Formula;
import com.cognitive.vo2calculator.Compartment;
import com.cognitive.vo2calculator.Vo2Formula;
import org.jcpsim.gui.InputElement;
import org.jcpsim.ode.DifferentialEquation;
import org.jcpsim.parameter.Input;
import org.jcpsim.parameter.Output;
import org.jcpsim.parameter.DeOutput;
import org.jcpsim.parameter.DEsOutput;
import org.jcpsim.plot.PlotNode;
import static org.jcpsim.units.Unit.*;
import org.jcpsim.run.Scenario;

import edu.umd.cs.piccolo.nodes.PText;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import org.jcpsim.clock.Clock;
import org.jcpsim.clock.ClockEvent;
import org.jcpsim.clock.ClockEventListener;
import org.jcpsim.gui.FrontpanelInput;
import org.jcpsim.plot.Trace;
import org.jcpsim.run.Global;

public class CustomRespirator extends Scenario implements ClockEventListener{

    private static final int TOP_PAD = 20;
    private static final Integer GRAPH_MAX_X = 50;
    
    // VENTILATOR INPUT PARAMETERS
    Input vPEEP;                // PEEP [mbar]
    Input vFrequency;           // frequency [/min]
    Input vInspiratoryTime;     // inspiratory time [%]
    Input vPauseTime;           // inspiratory pause [%]
    Input vPIP;                 // [mbar]
    Input vFIO2;
    Input vFlowRate;
    
    // PATIENT INPUT PARAMETERS
    Input pResistance;          // pulmonary resistance [mbar/(l/s)]
    Input pCompliance;          // pulmonary compliance [l/mbar]
    Input pVO2;
    Input pVCO2;
    Input pCardiacOutput;
    Input pHGB;
    Input pTemperature;
    Input pBarPressure;
    Input pShunt;
    Input pDeadSpace;
    Input pWeight;
    Input pOpeningPressure;

    
    // OUTPUT PARAMETERS
    Output oLungVolume;         // [l]
    Output oFlow;               // [l/s]
    Output oTidalVolume;        // tidal volume [l]
    Output oVQ;
    Output oPH;
    Output oPO2;
    Output oPCO2;
    Output oR;
    Output oPresp;
    Output oTV_Weight;
    Output oPEEPl;
    Output oAlveolarVentilation;
    Output oAaDO2;
    DEsOutput oCurrentSimulationTime;
    Output oAutoPEEP;    // [mbar]
    
    //GAS CALCULATION TIME OUTPUT PARAMETERS
    Input  ogGasCalculated;
    Output ogTV_Weight;
    Output ogPresp;
    Output ogPlung;
    Output ogTidalVolume;
    Output ogLungVolume;
    
    // state values
    DEsOutput oTime;        // [s]
    DeOutput oPlung;       // [mbar]
    
    // computed values
    double cycleTime;   // [s]
    double tInsp;       // [s]
    double tInspFlow;   // [s]
    double qsoll;       // [l/s]
    double tvi;
    

    
    // resulting values
    double timeInCycle; // [s]
    int Phase;       // [1|2|3]
    Block ventilatorBlock;
    Block patientBlock;
    Block runTimeBlock;
    Block runTimeOnGasCalculationBlock;
    int tPlot = 10000;  // [s]
    int fSampling = 50;  // [1/s]
    int traces = GRAPH_MAX_X * fSampling;
    PlotNode plot[];
    PText text;
    private boolean pauseRequested;
    private final Global global;
    private final Clock clock;
    
    private Vo2Formula vo2Formula = new CachedVo2Formula(100);
    private boolean paused;
    private boolean currentSimulationTimePaused = true;
    private long lastGasCalculationTime = 0;
    
    // ---------------------------------------------------------------------
    double tolerance = 0.000001;
    public int getPhase(double t) {
        timeInCycle = t % cycleTime;
        if (Double.compare(timeInCycle-tolerance, tInsp-tolerance) >= 0) {
            return 3; // exsp.
        }
        if (Double.compare(timeInCycle-tolerance, tInspFlow-tolerance) >= 0) {
            return 2; // insp. pause
        }
        return 1; // insp. flow
    }

    public void compute(int i) {
        //Presp.set(0);
        //V.set(0);
        //F.set(0);
    }

    public void step(int n) {
        oTime.stepDelta(1 / (double) fSampling);
        
        if (!currentSimulationTimePaused){
            oCurrentSimulationTime.stepDelta(1 / (double) fSampling/2);
        }
        for (PlotNode p : plot) {
            p.update();
        }
        ventilatorBlock.macroTimeStep(oTime.get());
        patientBlock.macroTimeStep(oTime.get());
        runTimeBlock.macroTimeStep(oTime.get());
    }

    // -----------------------------------------------------------------------------------
    public CustomRespirator(Global global, Clock clock) {

        super("Emory's Respirator");
        
        this.global = global;
        this.clock = clock;

        this.clock.addClockChangeListener(this);
        //register to topmenu
        this.global.getTopMenu().registerRespirator(this.global.getMode(), this);
        
        addBlock(ventilatorBlock = new Block("Ventilator", 0, TOP_PAD, this, global));
        vPIP = ventilatorBlock.addI("PIP", P_mbar, 10.0, 18, 50.0, 0.5, InputElement.InputMode.Click);
        vPEEP = ventilatorBlock.addI("PEEP", P_mbar, 0.0, 4.0, 20.0, 0.5, InputElement.InputMode.Click);
        vFrequency = ventilatorBlock.addI("Frequency", f_min, 4.0, 20.0, 120.0, 1.0, InputElement.InputMode.Click);
        vFIO2 = ventilatorBlock.addI("FIO2", _perc, 21, 30, 100, 1, FrontpanelInput.InputMode.Click);
        vInspiratoryTime = ventilatorBlock.addI("InspTime", _perc, 20.0, 35.0, 80.0, 1.0, InputElement.InputMode.Click);
        vPauseTime = ventilatorBlock.addI("PauseTime", _perc, 0.50, 10.0, 80.0, 1.0, InputElement.InputMode.Click);
        vFlowRate = ventilatorBlock.addI("FlowRate", F_l_m, 0, 60, 100, 5, FrontpanelInput.InputMode.Click);

        addBlock(patientBlock = new Block("Patient", 0, (int) (ventilatorBlock.getY() + ventilatorBlock.getHeight() + 5), this, global));
        pWeight = patientBlock.addI("Weight", m_kg, 1.0, 70.0, 150.0, 0.5, InputElement.InputMode.Click);
        pResistance = patientBlock.addI("Resistance", R_mbar_l_s, 2.0, 10.0, 100.0, 1.0, InputElement.InputMode.Click);
        pCompliance = patientBlock.addI("Compliance", C_l_mbar, 0.01, 0.04, 0.2, 0.0001, InputElement.InputMode.Click);
        pVO2 = patientBlock.addI("VO2", V_mls_min, 50, 200, 1000, 1, FrontpanelInput.InputMode.Click);
        pVCO2 = patientBlock.addI("VCO2", V_mls_min, 50, 150, 1000, 1, FrontpanelInput.InputMode.Click);
        pCardiacOutput = patientBlock.addI("CardiacOutput", F_l_m, 0.1, 5, 30, 0.1, FrontpanelInput.InputMode.Click);
        pHGB = patientBlock.addI("HemoglobinConc", CO_gm_dl, 1, 15, 20, 0.5, FrontpanelInput.InputMode.Click);
        pTemperature = patientBlock.addI("Temperature", T_c, 30, 37, 43, 0.5, FrontpanelInput.InputMode.Click);
        pBarPressure = patientBlock.addI("BarometricPressure", P_mmHg, 300, 760, 1520, 1, FrontpanelInput.InputMode.Click);
        pShunt = patientBlock.addI("Shunt", _perc, 3, 30, 80, 1, FrontpanelInput.InputMode.Click);
        pDeadSpace = patientBlock.addI("DeadSpace", _perc, 15, 20, 100, 1, FrontpanelInput.InputMode.Click);
        pOpeningPressure = patientBlock.addI("OpeningPressure", P_mbar, 3, 4, 20, 0.1, FrontpanelInput.InputMode.Click);
        

        addBlock(runTimeBlock = new Block("Runtime",(int)(global.getCanvas().getWidth()-ventilatorBlock.getWidth()-5), TOP_PAD, this, global));
        oTime = runTimeBlock.addD("t", t_s, 0, tPlot, 0.1, 0.0, 1.0);
        oCurrentSimulationTime = runTimeBlock.addD("currentSimulationTime", t_s, 0, tPlot, 0.1, 0.0, 1.0);
        oPH = runTimeBlock.addO("pH", no_unit, 0, 10, 0.01);
        oPO2 = runTimeBlock.addO("PO2", P_mmHg, 0, 200, 0.1);
        oPCO2 = runTimeBlock.addO("PCO2", P_mmHg, 0, 200, 0.1);
        oAlveolarVentilation = runTimeBlock.addO("AlveolarVentilation", F_l_m, 0, 20, 0.01);
        oTV_Weight = runTimeBlock.addO("TV/Weight", cc_kg, 0, 20 , 0.1);
        oAaDO2 = runTimeBlock.addO("AaDO2", P_mmHg, 0, 100, 0.01);
        oPresp = runTimeBlock.addO("Presp", P_mbar, 0, 40, 0.1);
        oPlung = oTime.add("LungPressure", P_mbar, 10.0, 50, 0.1, new DifferentialEquation() {
            
            double lowestPlung = 9999;
            double lowestPresp = 9999;
            boolean oPEEPlWasRecalculated;
            
            @Override
            public double initialValue() {
                return 25;
            }

            
            @Override
            public double dxdt(double t) {

                cycleTime = 60.0 / vFrequency.get();
                tInsp = 0.01 * cycleTime * vInspiratoryTime.get();
                tInspFlow = 0.01 * cycleTime * (vInspiratoryTime.get() - vPauseTime.get());
                qsoll = vFlowRate.get()/60;
                tvi = (vPIP.get() - vPEEP.get()) * pCompliance.get();
                
                

                switch (getPhase(t)) {
                    case 1: //insp flow
                        oPresp.set(get()+(qsoll)*pResistance.get());
                        oTidalVolume.set((get() - vPEEP.get()) * pCompliance.get());
                        oTV_Weight.set(oTidalVolume.get()/pWeight.get()*1000);
                        break;
                    case 2: //insp pause
                        oPresp.set(get());
                        if ((System.currentTimeMillis() - lastGasCalculationTime) >= 5000) {
                            System.out.println("Gas calculated at "+(System.currentTimeMillis() - lastGasCalculationTime));
                            lastGasCalculationTime = System.currentTimeMillis();
                            
                            if (oPEEPlWasRecalculated){
                                oAutoPEEP.set(lowestPlung-lowestPresp);
                                oPEEPl.set(lowestPlung);
                            }
                            oPEEPlWasRecalculated = false;
                            lowestPlung = 9999;
                            calculateVO2();
                        }
                        
                        break;
                    case 3: //exp
                        oPresp.set(vPEEP.get());
                        if (oPlung.get() < lowestPlung){
                            oPEEPlWasRecalculated = true;
                            lowestPlung = oPlung.get();
                            lowestPresp = oPresp.get();
                        }
                        
                        break;
                }
                    
                if (oPresp.get() > vPIP.get()){
                    oPresp.set(vPIP.get());
                }
                
                oLungVolume.set(get() * pCompliance.get());
                oFlow.set((oPresp.get() - get()) / pResistance.get());
          
                return oFlow.get() / pCompliance.get();
                
            }
        });   
        oPEEPl = runTimeBlock.addO("PEEPl", P_mbar, 0, 20 , 0.1);
        oAutoPEEP = runTimeBlock.addO("AutoPEEP", P_mbar, 0, 20 , 0.1);
        oFlow = runTimeBlock.addO("Flow", F_l_s, -2.5, 1.5, 0.01);
        
        addBlock(runTimeOnGasCalculationBlock = new Block("RuntimeGas",(int)(global.getCanvas().getWidth()-ventilatorBlock.getWidth()-5), (int)(runTimeBlock.getY() + runTimeBlock.getHeight() + 5), this, global));
        ogGasCalculated = runTimeOnGasCalculationBlock.addI("GasCalculated", no_unit, 0, 1 , 0, 1, InputElement.InputMode.Click);
        ogTV_Weight = runTimeOnGasCalculationBlock.addO("TV/Weight", cc_kg, 0, 20 , 0.1);
        oTidalVolume = runTimeOnGasCalculationBlock.addO("TidalVolume", V_l, 0.0, 1.5, 0.01);
        ogPresp = runTimeOnGasCalculationBlock.addO("Presp", P_mbar, 0, 50, 0.1);
        ogPlung = runTimeOnGasCalculationBlock.addO("LungPressure", P_mbar, 0.0, 40, 0.1);
        ogLungVolume = runTimeOnGasCalculationBlock.addO("Vlung", V_l, 0.0, 1.5, 0.01);
        oLungVolume = runTimeOnGasCalculationBlock.addO("Vlung", V_l, 0.0, 1.25, 0.01);
        oR = runTimeOnGasCalculationBlock.addO("RespiratoryQuotient", no_unit, 0, 2, 0.01);
        oVQ = runTimeOnGasCalculationBlock.addO("VQ", no_unit, 0, 3, 0.01);
        ogTidalVolume = runTimeOnGasCalculationBlock.addO("TidalVolume", V_l, 0.0, 1.5, 0.01);


        
        compute(0);

        int plotD = 5;    // distance between plots
        int plotW = (int) ((runTimeBlock.getX() - plotD) - (plotD + ventilatorBlock.getX() + ventilatorBlock.getWidth()));  // width of a plot
        int plotH = 200;  // height of a plot
        

        List<PlotNode> plotsList = new ArrayList<PlotNode>();

        plotsList.add(new PlotNode("Pressure", true, new Trace[]{new Trace("Plung", oTime, oPlung, traces, 1),
                new Trace("Presp", oTime, oPresp, traces, 1)}, GRAPH_MAX_X.doubleValue()));
        plotsList.add(new PlotNode("Volume", true, new Trace("Vlung", oTime, oLungVolume, traces, 1), GRAPH_MAX_X.doubleValue()));
        plotsList.add(new PlotNode("Flow", true, new Trace("Flow", oTime, oFlow, traces, 1), GRAPH_MAX_X.doubleValue()));

        
        plot = plotsList.toArray(new PlotNode[plotsList.size()]);

        for (int i = 0; i < plot.length; i++) {
            plot[i].setBounds(plotD + ventilatorBlock.getX()+ ventilatorBlock.getWidth(),
                    TOP_PAD + (i % 3) * (plotH + plotD), plotW, plotH);
            addChild(plot[i]);
        }

        setBounds(getUnionOfChildrenBounds(null));
        
        //new CustomRespiratorVitalSignsMonitor(this, 1).start();

    }

    public void requestPause() {
        this.pauseRequested = true;
    }

    public void resume() {
        this.pauseRequested = false;
        clock.resume();
    }

    private VO2CalculationDataSnaphot lastValidVO2CalculationInputData;
    
    private synchronized void setlLastValidVO2CalculationInputData(VO2CalculationDataSnaphot lastValidVO2CalculationInputData){
        this.lastValidVO2CalculationInputData = lastValidVO2CalculationInputData;
    }
    
    private synchronized void restoreLastValidVO2CalculationInputData(){
        if (lastValidVO2CalculationInputData == null){
            return;
        }
        
        lastValidVO2CalculationInputData.apply(this);
        
        repaintBlocks();
        
    }
    
    private void calculateVO2() {
        double newGasCalculatedValue = ogGasCalculated.get() == 0?1:0;
        try{
            if (paused){
                return;
            }

            final VO2CalculationDataSnaphot input = new VO2CalculationDataSnaphot();
            input.takeSnapshot(this);

            final double vqValue = 100 - input.getpShunt();
            // next line assumes deadspace flow is re-routed to the rest of the lung
            //final double vq = (input.getoTidalVolume() * input.getvFrequency() * vqValue/ input.getpCardiacOutput())/100;
            // next line assumes deadspace flow does not get re-routed
            final double vq = (input.getoTidalVolume() * input.getvFrequency() * vqValue * (1-input.getpDeadSpace()/100)/ input.getpCardiacOutput())/100;
            //System.out.println("V/Q= " + vq);
            if (Double.compare(vq, 100) > 1) {
                global.status("V/Q must be less than 100: " + vq);
                this.restoreLastValidVO2CalculationInputData();
                return;
            }

            if (Double.compare(vq, 0) < 1) {
                global.status("V/Q must be grater than 0: " + vq);
                this.restoreLastValidVO2CalculationInputData();
                return;
            }

            oVQ.set(vq);

            try {


                String[] indexes = new String[]{"0", "0.05", "0.1", "0.3", "0.5", vq+"", "2.0", "3.0", "6.0", "10.0", "999999"};
                double[] vqDistribution = {input.getpShunt(), 0, 0, 0, 0, vqValue, 0, 0, 0, 0, input.getpDeadSpace()};
                vo2Formula.setVo2(input.getpVO2());
                vo2Formula.setVco2(input.getpVCO2());
                vo2Formula.setCo(input.getpCardiacOutput());
                vo2Formula.setHgb(input.getpHGB());
                vo2Formula.setTemp(input.getpTemperature());
                vo2Formula.setPb(input.getpBarPressure());
                vo2Formula.setFiO2(input.getvFIO2() / 100);
                vo2Formula.setVqs(vqDistribution);
                vo2Formula.setVqsIndex(indexes);
                clock.pause(false);
                if (!input.equals(lastValidVO2CalculationInputData)){
                    System.out.println("TV= "+input.getoTidalVolume());
                    System.out.println("V/Q= "+vq+" -> "+vqValue);
                    System.out.println(vo2Formula);
                    List<Compartment> results = vo2Formula.compute();
                    
                    Compartment c = results.get(10);
                    
                    if (Double.isNaN(c.getPO2()) || Double.isNaN(c.getPCO2())){
                        throw new Exception("Unable to calculate blood gases!");
                    }
                    
                    //Set Runtime values
                    oPH.set(c.getPH());
                    oPO2.set(c.getPO2());
                    oPCO2.set(c.getPCO2());
                    oR.set(c.getR());
                    oAaDO2.set(c.getAaDO2());
                    oAlveolarVentilation.set((oTidalVolume.get()*(1-pDeadSpace.get()/100))* vFrequency.get());
                    global.status("");
                            
                    //set the values for Runtime Gas Calculation variables
                    ogTV_Weight.set(oTV_Weight.get());
                    ogLungVolume.set(oLungVolume.get());
                    ogPlung.set(oPlung.get());
                    ogPresp.set(oPresp.get());
                    ogTidalVolume.set(oTidalVolume.get());


                    setlLastValidVO2CalculationInputData(input);

                }

            } catch (ScriptException ex) {
                //javax.script.ScriptException: sun.org.mozilla.javascript.internal.JavaScriptException: The desired VO2 & VCO2 are not possible given the VQ distribution and FiO2.  Try again. (<Unknown source>#120) in <Unknown source> at line number 120
                String msg = ex.getMessage().substring(ex.getMessage().indexOf("JavaScriptException: ") + "JavaScriptException: ".length());
                msg = msg.substring(0, msg.indexOf(" (<")).replace('\n', ' ');
                restoreLastValidVO2CalculationInputData();
                if (vPIP.get() < 50) 
                  {vPIP.set(vPIP.get() + 0.25);}
                if (vFIO2.get() < 99)
                  {vFIO2.set(vFIO2.get() + 2.0);}
                //vFrequency.set(vFrequency.get() + 1.0);
                //vPEEP.set(vPEEP.get() + 1.0);
                global.status(msg);
                newGasCalculatedValue = 2;
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(CustomRespirator.class.getName()).log(Level.SEVERE, null, ex);
                restoreLastValidVO2CalculationInputData();
                global.status(ex.getMessage());
                newGasCalculatedValue = 2;
            } finally {
                clock.resume(false);
                if (pauseRequested){
                    //need to resume and pause again to notify listeners
                    clock.pause();
                } 
            }
        } finally{
            //change ogGasCalculated flag
            ogGasCalculated.set(newGasCalculatedValue);
            repaintBlocks();
        }
    }

    private void repaintBlocks() {
        for (Block block : this.getBlocks()) {
            block.invalidatePaint();
        }
    }

    public Clock getClock() {
        return clock;
    }

    public long getTime() {
        return clock.getCurrentTime();
    }

    public double getvPEEP() {
        return vPEEP.get();
    }

    public void setvPEEP(double value) {
        this.vPEEP.setDouble(value);
        this.repaintBlocks();
    }

    public double getvFrequency() {
        return vFrequency.get();
    }

    public void setvFrequency(double value) {
        this.vFrequency.setDouble(value);
        this.repaintBlocks();
    }

    public double getvInspiratoryTime() {
        return vInspiratoryTime.get();
    }

    public void setvInspiratoryTime(double value) {
        this.vInspiratoryTime.setDouble(value);
        this.repaintBlocks();
    }

    public double getvPauseTime() {
        return vPauseTime.get();
    }

    public void setvPauseTime(double value) {
        this.vPauseTime.setDouble(value);
        this.repaintBlocks();
    }

    public double getvPIP() {
        return vPIP.get();
    }

    public void setvPIP(double value) {
        this.vPIP.setDouble(value);
        this.repaintBlocks();
    }

    public double getvFIO2() {
        return vFIO2.get();
    }

    public void setvFIO2(double value) {
        this.vFIO2.setDouble(value);
        this.repaintBlocks();
    }

    public double getvFlowRate() {
        return vFlowRate.get();
    }

    public void setvFlowRate(double value) {
        this.vFlowRate.setDouble(value);
        this.repaintBlocks();
    }

    public double getpResistance() {
        return pResistance.get();
    }

    public void setpResistance(double value) {
        this.pResistance.setDouble(value);
        this.repaintBlocks();
    }

    public double getpCompliance() {
        return pCompliance.get();
    }

    public void setpCompliance(double value) {
        this.pCompliance.setDouble(value);
        this.repaintBlocks();
    }

    public double getpVO2() {
        return pVO2.get();
    }

    public void setpVO2(double value) {
        this.pVO2.setDouble(value);
        this.repaintBlocks();
    }

    public double getpVCO2() {
        return pVCO2.get();
    }

    public void setpVCO2(double value) {
        this.pVCO2.setDouble(value);
        this.repaintBlocks();
    }

    public double getpCardiacOutput() {
        return pCardiacOutput.get();
    }

    public void setpCardiacOutput(double value) {
        this.pCardiacOutput.setDouble(value);
        this.repaintBlocks();
    }

    public double getpHGB() {
        return pHGB.get();
    }

    public void setpHGB(double value) {
        this.pHGB.setDouble(value);
        this.repaintBlocks();
    }

    public double getpTemperature() {
        return pTemperature.get();
    }

    public void setpTemperature(double value) {
        this.pTemperature.setDouble(value);
        this.repaintBlocks();
    }

    public double getpBarPressure() {
        return pBarPressure.get();
    }

    public void setpBarPressure(double value) {
        this.pBarPressure.setDouble(value);
        this.repaintBlocks();
    }

    public double getpShunt() {
        return pShunt.get();
    }

    public void setpShunt(double value) {
        this.pShunt.setDouble(value);
        this.repaintBlocks();
    }

    public double getpWeight() {
        return pWeight.get();
    }

    public void setpWeight(double value) {
        this.pWeight.setDouble(value);
        this.repaintBlocks();
    }

    public double getpOpeningPressure() {
        return pOpeningPressure.get();
    }

    public void setpOpeningPressure(double value) {
        this.pOpeningPressure.setDouble(value);
        this.repaintBlocks();
    }

    public double getOgGasCalculated() {
        return ogGasCalculated.get();
    }

    public void setOgGasCalculated(double value) {
        this.ogGasCalculated.set(value);
        this.repaintBlocks();
    }

    public double getpDeadSpace() {
        return pDeadSpace.get();
    }

    public void setpDeadSpace(double pCshunt) {
        this.pDeadSpace.set(pCshunt);
        this.repaintBlocks();
    }

    public double getoLungVolume() {
        return oLungVolume.get();
    }

    public double getoFlow() {
        return oFlow.get();
    }

    public double getoTidalVolume() {
        return oTidalVolume.get();
    }

    public double getoVQ() {
        return oVQ.get();
    }

    public double getoPH() {
        return oPH.get();
    }

    public double getoPO2() {
        return oPO2.get();
    }

    public double getoPCO2() {
        return oPCO2.get();
    }

    public double getoR() {
        return oR.get();
    }

    public double getoPresp() {
        return oPresp.get();
    }

    public double getoTV_Weight() {
        return oTV_Weight.get();
    }

    public double getoPEEPl() {
        return oPEEPl.get();
    }
    
    public double getoAutoPEEP() {
        return oAutoPEEP.get();
    }
    
    public double getoPlung() {
        return oPlung.get();
    }

    public double getoAlveolarVentilation() {
        return oAlveolarVentilation.get();
    }

    public double getOgTV_Weight() {
        return ogTV_Weight.get();
    }

    public double getOgPresp() {
        return ogPresp.get();
    }

    public double getOgPlung() {
        return ogPlung.get();
    }

    public double getOgTidalVolume() {
        return ogTidalVolume.get();
    }

    public double getOgLungVolume() {
        return ogLungVolume.get();
    }

    public double getoAADO2() {
        return oAaDO2.get();
    }
    
    public void resetCurrentSimulationTime(){
        this.oCurrentSimulationTime.setT(0.0);
        this.currentSimulationTimePaused = false;
        lastGasCalculationTime = 0;
    }
    
    public void stopCurrentSimulationTime(){
        this.currentSimulationTimePaused = true;
    }
    
    public void onEvent(ClockEvent event) {
        switch (event.getType()){
            case CLOCK_PAUSED:
                paused = true;
                break;
            case CLOCK_RESUMED:
                paused = false;
                break;
        }
    }

    
}
