/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.recommendation.gnome;

/**
 *
 * @author esteban
 */
public class NoSNPFoundException extends RuntimeException {

    public NoSNPFoundException(String rsid) {
        super("No SNP found with RSID '"+rsid+"'");
    }
    
}
