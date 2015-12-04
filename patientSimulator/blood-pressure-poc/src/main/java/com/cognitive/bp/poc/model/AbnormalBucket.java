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
