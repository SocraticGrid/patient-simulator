/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitive.bp.poc.model;

import java.util.ArrayList;
import java.util.List;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class PeaksBucket {
    public static final int MAX_SIZE = 5;
    
    private List<PeakJCpSimData> peaks = new ArrayList<>();

    public List<PeakJCpSimData> getPeaks() {
        return peaks;
    }

    public void setPeaks(List<PeakJCpSimData> peaks) {
        this.peaks = peaks == null? new ArrayList<PeakJCpSimData>() : peaks;
    }
    
    public void addPeak(PeakJCpSimData peak) {
        if (this.isFull()){
            throw new IllegalStateException("PeaksBucket is already full!");
        }
        this.peaks.add(peak);
    }

    public boolean isFull(){
        return peaks.size() == MAX_SIZE;
    }
 
    public double getStandardDeviation(JCpSimParameter target){
        double total = 0.0;
        
        for (PeakJCpSimData p : peaks) {
            double value = p.getData().get(target) == null ? 0.0  : p.getData().get(target);
            total += value;
        }
        
        double mean = total / peaks.size();
        
        
        List<Double> listOfDifferences = new ArrayList<>();
        for (PeakJCpSimData p : peaks) {
            double value = p.getData().get(target) == null ? 0.0  : p.getData().get(target);
            double difference = value - mean;
            listOfDifferences.add(difference);
        }
        
        List<Double> squares = new ArrayList<>();
        for(double difference : listOfDifferences) {
            double square = difference * difference;
            squares.add(square);
        }
        
        double sum = 0;
        for(double number : squares) {
            sum = sum + number;
        }

        
        double result = sum / (peaks.size() - 1);
        
        return Math.sqrt(result);
        
    }
    
}
