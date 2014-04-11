/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.vo2calculator;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class TimeoutTest {
    
    
    private void configureFormula(Vo2Formula f){
        String[] vqsIndex = new String[]{"0", "0.05", "0.1", "0.3", "0.5", "1.67", "2.0", "3.0", "6.0", "10.0", "999999"};
        double[] vqDistribution = {
            0,      //0
            0,      //0.05
            0,      //0.5
            2.7,
            18.7,
            69,
            8.4,
            1.3,
            0,
            0,
            15};
        
        double vO2 = 250;
        double vCO2 = 200;
        double cardiacOutput = 5;
        double hgb = 15;
        double bt = 37;
        double bp = 760;
        double fiO2 = 0.21;
        
        f.setVo2(vO2);
        f.setVco2(vCO2);
        f.setCo(cardiacOutput);
        f.setHgb(hgb);
        f.setTemp(bt);
        f.setPb(bp);
        f.setFiO2(fiO2);
        f.setVqs(vqDistribution);
        
    }
    
    @Test
    public void testTimeout() throws Exception{
        
        Vo2Formula f = new Vo2Formula();
        
        this.configureFormula(f);
        
        List<Compartment> results = f.compute();
        
        for (Compartment r : results) {
            System.out.printf("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f\n", r.getVQ(), r.getQ(), r.getPH(), r.getPO2(), r.getPCO2(), r.getVN2(), r.getCaO2(), r.getCaCO2(), r.getR() );
        }
        
        
    }
    
}
