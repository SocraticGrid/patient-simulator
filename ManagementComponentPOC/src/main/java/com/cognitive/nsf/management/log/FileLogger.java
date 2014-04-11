/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.log;

import com.cognitive.data.ValueModifiedToken;
import com.cognitive.nsf.management.fact.ModelRecommendationApplied;
import com.cognitive.nsf.management.model.DiseaseModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class FileLogger {

    private static FileLogger INSTANCE;
    private PrintWriter modelWriter;
    private final PrintWriter mixedWriter;
    private final PrintWriter diseaseWriter;
    private boolean firstLineExists;
    private DecimalFormat df = new DecimalFormat("#0.000");
    DateFormat timeFormatter = new SimpleDateFormat("kk:mm:ss:SS");
    private String[] logHeaders = new String[]{
        "TIME",
        JCpSimParameter.V_PIP.name(),
        JCpSimParameter.V_PEEP.name(),
        JCpSimParameter.V_FREQUENCY.name(),
        JCpSimParameter.P_RESISTANCE.name(),
        JCpSimParameter.P_COMPLIANCE.name(),
        JCpSimParameter.P_SHUNT.name(),
        JCpSimParameter.P_DEADSPACE.name(),
        JCpSimParameter.P_OPENING_PRESSURE.name(),
        JCpSimParameter.O_G_TV_WEIGHT.name(),
        JCpSimParameter.O_PH.name(),
        JCpSimParameter.O_PCO2.name(),
        JCpSimParameter.O_PO2.name(),
        JCpSimParameter.V_FIO2.name(),
        JCpSimParameter.O_G_GAS_CALCULATED.name(),
        "Model",
        "Recommendation Rule",
        JCpSimParameter.V_INSPIRATORY_TIME.name(),
        JCpSimParameter.V_PAUSE_TIME.name(),
        JCpSimParameter.V_FLOW_RATE.name(),
        JCpSimParameter.P_VO2.name(),
        JCpSimParameter.P_VCO2.name(),
        JCpSimParameter.P_CARDIAC_OUTPUT.name(),
        JCpSimParameter.P_HGB.name(),
        JCpSimParameter.P_TEMPERATURE.name(),
        JCpSimParameter.P_BAR_PRESSURE.name(),
        JCpSimParameter.P_WEIGHT.name(),
        JCpSimParameter.O_PLUNG.name(),
        JCpSimParameter.O_FLOW.name(),
        JCpSimParameter.O_LUNG_VOLUME.name(),
        JCpSimParameter.O_PRESP.name(),
        JCpSimParameter.O_R.name(),
        JCpSimParameter.O_TV_WEIGHT.name(),
        JCpSimParameter.O_PEEP_L.name(),
        JCpSimParameter.O_AutoPEEP.name(),
        JCpSimParameter.O_TIDAL_VOLUME.name(),
        JCpSimParameter.O_VQ.name(),
        JCpSimParameter.O_MINUTE_VENTILATION.name(),
        JCpSimParameter.O_AADO2.name(),
        JCpSimParameter.O_G_PLUNG.name(),
        JCpSimParameter.O_G_LUNG_VOLUME.name(),
        JCpSimParameter.O_G_PRESP.name(),
        JCpSimParameter.O_G_TIDAL_VOLUME.name()
    };

    public synchronized static FileLogger init(String modelLogFile, String diseaseLogFile, String mixedLogFile) throws IOException {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }

        INSTANCE = new FileLogger(modelLogFile, diseaseLogFile, mixedLogFile);
        return INSTANCE;
    }

    public synchronized static FileLogger getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Not yet initialized");
        }

        return INSTANCE;
    }
    

    private FileLogger(String modelLogFile, String diseaseLogFile, String mixedLogFile) throws IOException {
        this(new File(modelLogFile), new File(diseaseLogFile), new File(mixedLogFile));
    }

    private FileLogger(File modelLogFile, File diseaseLogFile, File mixedLogFile) throws IOException {
        modelWriter = new PrintWriter(new BufferedWriter(new FileWriter(modelLogFile)));
        diseaseWriter = new PrintWriter(new BufferedWriter(new FileWriter(diseaseLogFile)));
        mixedWriter = new PrintWriter(new BufferedWriter(new FileWriter(mixedLogFile)));
    }

    public synchronized void logData(Map<JCpSimParameter, Double> data, ModelRecommendationApplied lastRecommendation) throws IOException {

        //log headers
        this.printHeadersIfRequired();

        //log data on model log
        this.printData(data, lastRecommendation, modelWriter);
        
        //log data on mixed log
        this.printData(data, lastRecommendation, mixedWriter);
    }

    public synchronized void logData(ModelRecommendationApplied lastRecommendation) throws IOException {
        this.logData(null, lastRecommendation);
    }

    public synchronized void logData(String disease, ValueModifiedToken valueModifiedToken) throws IOException {
        //log headers
        this.printHeadersIfRequired();
        
        //log data on disease log
        this.printData(disease, valueModifiedToken, diseaseWriter);
        
        //log data on mixed log
        this.printData(disease, valueModifiedToken, mixedWriter);
    }
    
    public synchronized void logMissingRecommendation(Map<JCpSimParameter, Double> data, DiseaseModel model) throws IOException{
        //log headers
        this.printHeadersIfRequired();
        
        
        //create mock ModelRecommendationApplied for logging purposes
        EnumMap<JCpSimParameter, List<String>> sources = new EnumMap<JCpSimParameter, List<String>>(JCpSimParameter.class);
        List<String> s = new ArrayList<String>();
        s.add("NO RECOMMENDATION!");
        sources.put(data.keySet().iterator().next(), s);
        
        ModelRecommendationApplied mock = new ModelRecommendationApplied(
                model, 
                new EnumMap<JCpSimParameter, Double>(JCpSimParameter.class), 
                sources);
        
        //log data on disease log
        this.printData(data, mock, diseaseWriter);
        
        //log data on mixed log
        this.printData(data, mock, mixedWriter);
    }
    
    private void printHeadersIfRequired() throws IOException {
        if (!firstLineExists) {
            String separator = "";
            for (String header : logHeaders) {
                modelWriter.append(separator);
                modelWriter.append(header);
                diseaseWriter.append(separator);
                diseaseWriter.append(header);
                mixedWriter.append(separator);
                mixedWriter.append(header);
                if (separator.equals("")) {
                    separator = ",";
                }
            }
            modelWriter.append("\n");
            diseaseWriter.append("\n");
            mixedWriter.append("\n");
        }
        firstLineExists = true;
    }
    
    private void printData(String disease, ValueModifiedToken valueModifiedToken, PrintWriter writer) throws IOException {
        String separator = "";
        try {
            for (String h : logHeaders) {
                writer.append(separator);
                if (h.equals("TIME")) {
                    writer.append(timeFormatter.format(new Date(System.currentTimeMillis())));
                } else if (h.equals("Model")) {
                    writer.append(disease);
                } else if (h.equals("Recommendation Rule")) {
                    writer.append(valueModifiedToken.getSource());
                } else {
                    if (valueModifiedToken.getTarget() == JCpSimParameter.valueOf(h)){
                        writer.append(df.format(valueModifiedToken.getNewValue()));
                    } else{
                        writer.append("x");
                    }
                }
                separator = ",";

            }
        } catch (Exception e) {
            writer.append("\n(" + valueModifiedToken + "):\n");
            e.printStackTrace(writer);
            writer.append("\n");
        }
        writer.append("\n");
        writer.flush();
    }

    private void printData(Map<JCpSimParameter, Double> data, ModelRecommendationApplied lastRecommendation, PrintWriter writer) throws IOException {

        String separator = "";
        for (String h : logHeaders) {
            try {
                writer.append(separator);
                if (h.equals("TIME")) {
                    writer.append(timeFormatter.format(new Date(System.currentTimeMillis())));
                } else if (h.equals("Model")) {
                    writer.append(lastRecommendation.getModel().getName());
                } else if (h.equals("Recommendation Rule")) {
                    printRecommendationRuleContent(lastRecommendation, writer);
                } else {
                    Double v;
                    if (data != null) {
                        v = data.get(JCpSimParameter.valueOf(h));
                    } else {
                        v = lastRecommendation.getAppliedChanges().get(JCpSimParameter.valueOf(h));
                    }

                    if (v != null) {
                        writer.append(df.format(v));
                    } else {
                        writer.append("-");
                    }

                }

                separator = ",";
            } catch (Exception e) {
                writer.append("\n" + h + "(" + data.get(JCpSimParameter.valueOf(h)) + "):\n");
                e.printStackTrace(writer);
                writer.append("\n");
            }
        }

        writer.append("\n");
        writer.flush();
    }

    private void printRecommendationRuleContent(ModelRecommendationApplied lastRecommendation, PrintWriter writer) throws IOException {


        Set<List<String>> sources = new HashSet<List<String>>(lastRecommendation.getSources().values());

        //eliminate duplicated sources
        Set<String> uniqueSources = new HashSet<String>();
        for (List<String> sourceList : sources) {
            uniqueSources.addAll(sourceList);
        }
        
        String separator = "";
        for (String source : uniqueSources) {
            writer.append(separator + source);
            separator = " | ";
        }
    }

    public synchronized void dispose() throws IOException {
        INSTANCE = null;
        modelWriter.close();
        diseaseWriter.close();
        mixedWriter.close();
    }
}
