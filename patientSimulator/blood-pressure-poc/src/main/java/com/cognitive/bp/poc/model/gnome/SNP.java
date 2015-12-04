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
package com.cognitive.bp.poc.model.gnome;

/**
 *
 * @author esteban
 */
public class SNP {
    public final String rsid;
    public final int position;
    public final String genotype;
    public final String chromosome;

    public SNP(String rsid, int position, String genotype, String chromosome) {
        this.rsid = rsid;
        this.position = position;
        this.genotype = genotype;
        this.chromosome = chromosome;
    }

    public String getRsid() {
        return rsid;
    }

    public int getPosition() {
        return position;
    }

    public String getGenotype() {
        return genotype;
    }

    public String getChromosome() {
        return chromosome;
    }

    @Override
    public String toString() {
        return "SNP{" + "rsid=" + rsid + ", position=" + position + ", genotype=" + genotype + ", chromosome=" + chromosome + '}';
    }
 
}
