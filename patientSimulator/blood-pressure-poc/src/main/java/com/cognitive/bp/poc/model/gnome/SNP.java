/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
