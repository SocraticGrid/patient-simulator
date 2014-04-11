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
public class Vo2FormulaTest {
    
    
    private void configureCase1Formula(Vo2Formula f){
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
        f.setVqsIndex(vqsIndex);
        
    }
    
    private void configureCase2Formula(Vo2Formula f){
        String[] vqsIndex = new String[]{"0", "0.05", "0.1", "0.3", "0.5", "1.0", "2.0", "3.0", "6.0", "10.0", "999999"};
        double[] vqDistribution = {0.0,0.0,0.0,0.0,0.0,100,0.0,0.0,0.0,0.0,15};
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
        f.setVqsIndex(vqsIndex);
        
    }

    @Test
    public void testCase1() throws Exception{
        
        Vo2Formula f = new Vo2Formula();
        
        this.configureCase1Formula(f);
        
        List<Compartment> results = f.compute();
        
        for (Compartment r : results) {
            System.out.printf("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f\n", r.getVQ(), r.getQ(), r.getPH(), r.getPO2(), r.getPCO2(), r.getVN2(), r.getCaO2(), r.getCaCO2(), r.getR(), r.getAaDO2() );
        }
        
        
    }
    
    @Test
    public void testCase2() throws Exception{
        
        Vo2Formula f = new Vo2Formula();
        
        this.configureCase2Formula(f);
        
        List<Compartment> results = f.compute();
        
        for (Compartment r : results) {
            System.out.printf("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f\n", r.getVQ(), r.getQ(), r.getPH(), r.getPO2(), r.getPCO2(), r.getVN2(), r.getCaO2(), r.getCaCO2(), r.getR(), r.getAaDO2()  );
        }
        
        
    }
    
    @Test
    public void testCase3() throws Exception{
        
        Vo2Formula f1 = new Vo2Formula();
        this.configureCase1Formula(f1);
        
        Vo2Formula f2 = new Vo2Formula();
        this.configureCase2Formula(f2);
        
        //independent results
        List<Compartment> results1 = f1.compute();
        List<Compartment> results2 = f2.compute();
        
        
        //formula reuse
        this.configureCase2Formula(f1);
        List<Compartment> results3 = f1.compute();

        Assert.assertArrayEquals(results2.toArray(), results3.toArray());
        
        this.configureCase1Formula(f1);
        List<Compartment> results4 = f1.compute();

        Assert.assertArrayEquals(results1.toArray(), results4.toArray());
        
        
        
    }
    
    @Test
    public void testCase4() throws Exception{
        
        String[] indexes = new String[]{"0", "0.05", "0.1", "0.3", "0.5", "11", "2.0", "3.0", "6.0", "10.0", "999999"};
                
        Vo2Formula f = new Vo2Formula();
        
        this.configureCase2Formula(f);
        f.setVqsIndex(indexes);
        
        List<Compartment> results = f.compute();
        
        for (Compartment r : results) {
            System.out.printf("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f\n", r.getVQ(), r.getQ(), r.getPH(), r.getPO2(), r.getPCO2(), r.getVN2(), r.getCaO2(), r.getCaCO2(), r.getR() );
        }
        
    }
}
