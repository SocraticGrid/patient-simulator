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
public class AbnormalBucket {

    private final long start;
    private final long end;
    private final long duration;
    private final double sd;
    private boolean processed;

    public AbnormalBucket(PeaksBucket source, double sd) {
        start = source.getPeaks().get(0).getTimestamp();
        end = source.getPeaks().get(source.getPeaks().size()-1).getTimestamp();
        duration = end - start;
        this.sd = sd;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public double getSd() {
        return sd;
    }

    @Override
    public String toString() {
        return "AbnormalBucket{" + "start=" + start + ", end=" + end + ", duration=" + duration + ", sd=" + sd + ", processed=" + processed + '}';
    }

}
