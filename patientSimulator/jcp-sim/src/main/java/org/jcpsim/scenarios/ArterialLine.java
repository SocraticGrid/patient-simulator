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

import com.cognitive.logger.ArterialLineDataFileLogger;
import java.awt.Color;

import org.jcpsim.ode.DifferentialEquation;
import org.jcpsim.parameter.Input;
import org.jcpsim.parameter.Output;
import org.jcpsim.parameter.DeOutput;
import org.jcpsim.parameter.DEsOutput;
import org.jcpsim.plot.PlotNode;
import org.jcpsim.plot.Trace;
import static org.jcpsim.units.Unit.*;
import org.jcpsim.util.Bezier;
import org.jcpsim.run.Scenario;
import org.jcpsim.gui.InputElement;

import edu.umd.cs.piccolo.nodes.PText;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jcpsim.clock.Clock;
import org.jcpsim.clock.ClockEvent;
import org.jcpsim.clock.ClockEventListener;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.run.Global;

public class ArterialLine extends Scenario implements ClockEventListener {

    public static interface ArterialLineEventListener {

        public void onDataCalculated(JCpSimData data);
    }

    // given values
    Input WavesToShow;      // wavwforms to be shown
    Input Wave;      // number of wavwform
    Input Flush;     // flush [yes|no]
    Input Damp;      // correct - underdamped - overdamped    
    Input Rline;     // resistance
    Input Cline;     // compliance 
    Input Lline;     // inductance

    Input pPatientMod;

    Output Freq;      // natural frequency Fn
    Output DampCoeff; // damping coefficient

    // state values
    DEsOutput time;      // 
    DeOutput qLine;     // 
    DeOutput pMeas;     //

    //traces
    Trace trace1;
    Trace trace2;
    Color trace1DefaultColor = new Color(0, 0, 255, 128);
    Color trace1HiddenColor = Color.GREEN;
    Color trace2DefaultColor = new Color(255, 0, 0, 128);
    Color trace2HiddenColor = Color.ORANGE;

    final double RflushTrue = 0.2;
    final double RflushFalse = 200 * 3600 / 2;
    final double pFlush = 300;

    // resulting values
    double Rflush;
    double qFlush;

    Output _pPatient;
    Output _pMeas;

    PlotNode plot;
    PText text;

    Block block;

    int tPlot = 8;  // [s]
    int fSampling = 50;  // [1/s]
    int traces = tPlot * fSampling;

    Bezier broadWave;
    Bezier narrowWave;
    private final Global global;

    ArterialLineDataFileLogger fileLogger;

    private final Clock clock;
    private boolean paused;
    private boolean currentSimulationTimePaused = true;
    private boolean pauseRequested;

    private List<ArterialLineEventListener> eventListeners = Collections.synchronizedList(new ArrayList<ArterialLineEventListener>());

    // ---------------------------------------------------------------------
    private class broadBezier extends Bezier {

        public broadBezier() {
            super(1, 337);
            add(8.55, 341.27, 11.90, 330.94, 13.57, 325.00);
            add(18.53, 307.42, 20.69, 288.08, 23.27, 270.00);
            add(31.93, 209.24, 40.55, 147.13, 46.00, 86.00);
            add(47.37, 70.63, 49.39, 52.22, 55.63, 38.00);
            add(57.70, 33.27, 59.74, 26.13, 65.04, 24.17);
            add(72.53, 21.40, 83.52, 36.56, 89.00, 40.98);
            add(94.43, 45.38, 101.00, 45.28, 106.99, 48.45);
            add(121.25, 56.00, 138.99, 69.63, 149.56, 82.00);
            add(179.93, 117.57, 177.93, 168.19, 210.17, 201.96);
            add(228.96, 221.64, 253.71, 214.88, 276.23, 225.49);
            add(293.18, 233.67, 303.90, 249.98, 317.51, 262.57);
            add(325.69, 270.14, 337.24, 273.73, 346.00, 280.67);
            add(360.54, 292.18, 373.44, 305.92, 389.00, 316.50);
            add(394.58, 320.30, 402.03, 321.46, 408.00, 324.87);
            add(414.75, 328.73, 430.56, 342.48, 433.00, 327.00);
        }

        public double get(double x) {
            return 160 - getY(x) * 0.25;
        }
    }

    private class narrowBezier extends Bezier {

        public narrowBezier() {
            super(1, 331);
            add(10.27, 331.72, 11.23, 322.08, 13.15, 315.00);
            add(18.02, 297.00, 21.56, 278.47, 24.13, 260.00);
            add(32.57, 199.43, 36.94, 138.38, 46.92, 78.00);
            add(49.34, 63.35, 51.10, 48.32, 55.16, 34.00);
            add(56.51, 29.21, 59.36, 21.33, 64.10, 18.89);
            add(70.42, 15.65, 79.81, 33.57, 82.39, 38.00);
            add(90.52, 51.94, 92.00, 72.48, 96.43, 88.00);
            add(99.24, 97.84, 105.10, 105.62, 108.45, 115.00);
            add(116.67, 137.98, 128.85, 158.86, 137.58, 182.00);
            add(150.07, 215.12, 158.38, 257.45, 191.00, 277.53);
            add(201.44, 283.96, 205.87, 275.84, 215.00, 271.27);
            add(222.46, 267.54, 232.88, 266.48, 241.00, 268.79);
            add(254.52, 272.63, 266.49, 283.81, 278.00, 291.49);
            add(301.89, 307.41, 327.36, 321.38, 349.00, 340.00);
        }

        public double get(double x) {
            return 160 - getY(x) * 0.25;
        }
    }

    public double pPatient(double t) {
        switch ((int) Wave.get()) {
            case 0:
                return broadWave.get(t) + pPatientMod.get();
            case 1:
                return narrowWave.get(t) + pPatientMod.get();
            default:
                return 100;
        }
    }

    public void compute(int i) {
        _pPatient.set(pPatient(0));
    }

    int oldWavesToShowValue = -1;

    public void step(int n) {
        time.stepDelta(1 / (double) fSampling);
        _pPatient.set(pPatient(time.get()));
        plot.update();
        block.macroTimeStep(time.get());

        if (oldWavesToShowValue != (int) WavesToShow.get()) {
            oldWavesToShowValue = (int) WavesToShow.get();
            switch ((int) WavesToShow.get()) {
                case 0:
                    trace1.color = trace1DefaultColor;
                    trace2.color = trace2DefaultColor;
                    break;
                case 1:
                    trace1.color = trace1DefaultColor;
                    trace2.color = plot.getClipColor();
                    break;
                default:
                    trace1.color = plot.getClipColor();
                    trace2.color = trace2DefaultColor;
                    break;
            }
        }

    }

    // -----------------------------------------------------------------------------------
    public ArterialLine(Global global, Clock clock) {
        super("Arterial Line");

        try {
            this.fileLogger = new ArterialLineDataFileLogger(File.createTempFile("ArterialLine", ".csv"));
        } catch (IOException ex) {
            Logger.getLogger(ArterialLine.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.global = global;
        this.clock = clock;

        this.clock.addClockChangeListener(this);
        //register to topmenu
        this.global.getTopMenu().registerArterialLine(this.global.getMode(), this);

        broadWave = new broadBezier();
        narrowWave = new narrowBezier();
        addBlock(block = new Block("ArterialLine", 710, 0, this, global));

        WavesToShow = block.addI("Show", no_unit, 0.0, 0.0, 2.0, 1.0, InputElement.InputMode.Click);
        Wave = block.addI("Wave", no_unit, 0.0, 0.0, 2.0, 1.0, InputElement.InputMode.Click);
        Flush = block.addI("Flush", no_unit, 0.0, 0.0, 1.0, 1.0, InputElement.InputMode.Press);
        Damp = block.addI("Damping", no_unit, 0.0, 0.0, 2.0, 1.0, InputElement.InputMode.Click);
        Rline = block.addI("Resistance", R_mmHg_ml_s, 0.06, 0.58, 4.0, 0.02, InputElement.InputMode.Drag);
        Lline = block.addI("Inertance", L_ss_mmHg_ml, 0.002, 0.02, 0.04, 0.002, InputElement.InputMode.Drag);
        Cline = block.addI("Compliance", C_ml_mmHg, 0.01, 0.3, 0.4, 0.01, InputElement.InputMode.Drag);
        pPatientMod = block.addI("Mod", no_unit, -40, 0.0, 40, 1.0, InputElement.InputMode.Drag);
        time = block.addD("t", t_s, 0, tPlot, 0.01, 0.0, 1.0);

        qLine = time.add("Flow", F_ml_s, -1.0, 1.0, 0.01,
                new DifferentialEquation() {
                    public double initialValue() {
                        return 0;
                    }

                    public double dxdt(double t) {
                        return (pPatient(t) - Rline.get() * get() - pMeas.get()) / Lline.get();
                    }
                }
        );

        pMeas = time.add("Presp", P_mmHg, 0, 300, 0.1,
                new DifferentialEquation() {
                    public double initialValue() {
                        return pPatient(0);
                    }

                    public double dxdt(double t) {
                        Rflush = (Flush.get() < 0.5) ? RflushFalse : RflushTrue;
                        qFlush = (pFlush - pMeas.get()) / Rflush;
                        return (qLine.get() + qFlush) / Cline.get();
                    }
                }
        );

        Freq = block.addO(new Output(block, "Frequency", f_Hz, 0.0, 50.0, 0.1) {
            public double get() {
                if (fileLogger != null) {
                    try {
                        fileLogger.write(pMeas.get(), _pPatient.get(), pPatientMod.get());
                    } catch (IOException ex) {
                        Logger.getLogger(ArterialLine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                JCpSimDataImpl data = new JCpSimDataImpl();

                //Patient data
                data.set(JCpSimParameter.AA_P_WAVE, getAAPWAVE());
                data.set(JCpSimParameter.AA_P_FLUSH, getAAPFLUSH());
                data.set(JCpSimParameter.AA_P_DAMP, getAAPDAMP());
                data.set(JCpSimParameter.AA_P_RLINE, getAAPRLINE());
                data.set(JCpSimParameter.AA_P_CLINE, getAAPCLINE());
                data.set(JCpSimParameter.AA_P_LLINE, getAAPLLINE());
                data.set(JCpSimParameter.AA_P_MOD, getAAPMOD());

                //Output data
                data.set(JCpSimParameter.AA_O_FLOW, getAAOFLOW());
                data.set(JCpSimParameter.AA_O_PRESP, getAAOPRESP());
                //data.set(JCpSimParameter.AA_O_FREQ, getAAOFREQ());
                data.set(JCpSimParameter.AA_O_DAMP_COEFF, getAAODAMPCOEFF());
                data.set(JCpSimParameter.AA_O_PREAL, getAAOPREAL());

                //time
                data.setTime(System.currentTimeMillis());

                for (ArterialLineEventListener arterialLineEventListener : eventListeners) {
                    arterialLineEventListener.onDataCalculated(data);
                }

                return 1.0 / (2.0 * Math.PI * Math.sqrt(Lline.get() * Cline.get()));
            }
        });

        DampCoeff = block.addO(new Output(block, "DampingCoeff", no_unit, 0.0, 10.0, 0.01) {
            public double get() {
                return Rline.get() / 2.0 * Math.sqrt(Cline.get() / Lline.get());
            }
        });

        _pPatient = block.addO("Preal", P_mmHg, 0, 300, 0.1);

        compute(0);

        trace1 = new Trace("at Patient", time, _pPatient, traces, 2, 5.0f, trace1DefaultColor, null, Trace.LINES, null);
        trace2 = new Trace("at Transducer", time, pMeas, traces, 2, 5.0f, trace2DefaultColor, null, Trace.LINES, null);

        plot = new PlotNode("Arterial Line Plot", true, new Trace[]{trace1, trace2});

        plot.setBounds(0, 0, 700, 500);
        addChild(plot);
        setBounds(getUnionOfChildrenBounds(null));
    }

    public void addEventListener(ArterialLineEventListener listener) {
        this.eventListeners.add(listener);
    }

    public void requestPause() {
        //TODO: pasue only on peaks? 
        clock.pause();
    }

    public void resume() {
        this.pauseRequested = false;
        clock.resume();
    }

    public Clock getClock() {
        return clock;
    }

    public void resetCurrentSimulationTime() {
        try {
            this.time.setT(0.0);
            this.currentSimulationTimePaused = false;

            this.fileLogger = new ArterialLineDataFileLogger(File.createTempFile("ArterialLine", ".csv"));
        } catch (IOException ex) {
            Logger.getLogger(ArterialLine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void stopCurrentSimulationTime() {
        this.currentSimulationTimePaused = true;
        fileLogger = null;
    }

    private void repaintBlocks() {
        for (Block block : this.getBlocks()) {
            block.invalidatePaint();
        }
    }

    public Double getAAPWAVE() {
        return this.Wave.get();
    }

    public Double getAAPFLUSH() {
        return this.Flush.get();
    }

    public Double getAAPDAMP() {
        return this.Damp.get();
    }

    public Double getAAPRLINE() {
        return this.Rline.get();
    }

    public Double getAAPCLINE() {
        return this.Cline.get();
    }

    public Double getAAPLLINE() {
        return this.Lline.get();
    }

    public Double getAAPMOD() {
        return this.pPatientMod.get();
    }

    public Double getAAOFLOW() {
        return this.qLine.get();
    }

    public Double getAAOPRESP() {
        return this.pMeas.get();
    }

    public Double getAAOFREQ() {
        return this.Freq.get();
    }

    public Double getAAODAMPCOEFF() {
        return this.DampCoeff.get();
    }

    public Double getAAOPREAL() {
        return this._pPatient.get();
    }

    public long getTime() {
        return clock.getCurrentTime();
    }

    public void setAAPWAVE(double value) {
        this.Wave.setDouble(value);
        this.repaintBlocks();
    }

    public void setAAPFLUSH(double value) {
        this.Flush.setDouble(value);
        this.repaintBlocks();
    }

    public void setAAPDAMP(double value) {
        this.Damp.setDouble(value);
        this.repaintBlocks();
    }

    public void setAAPRLINE(double value) {
        this.Rline.setDouble(value);
        this.repaintBlocks();
    }

    public void setAAPCLINE(double value) {
        this.Cline.setDouble(value);
        this.repaintBlocks();
    }

    public void setAAPLLINE(double value) {
        this.Lline.setDouble(value);
        this.repaintBlocks();
    }

    public void setAAPMOD(double value) {
        this.pPatientMod.setDouble(value);
        this.repaintBlocks();
    }

    public void onEvent(ClockEvent event) {
        switch (event.getType()) {
            case CLOCK_PAUSED:
                paused = true;
                break;
            case CLOCK_RESUMED:
                paused = false;
                break;
        }
    }
}
