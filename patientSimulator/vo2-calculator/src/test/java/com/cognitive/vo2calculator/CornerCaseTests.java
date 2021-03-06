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
package com.cognitive.vo2calculator;

import java.util.List;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class CornerCaseTests {
    
    @Test
    public void testProblematicParameters() throws Exception{
        //vqsIndex=[0,0.05,0.1,0.3,0.5,1.039243865442647,2.0,3.0,6.0,10.0,999999,], vqs=[35.0,0.0,0.0,0.0,0.0,65.0,0.0,0.0,0.0,0.0,15.0,], engine=null}
        
        Vo2Formula f = new Vo2Formula();
        
        String[] vqsIndex = new String[]{"0", "0.05", "0.1", "0.3", "0.5", "1.039", "2.0", "3.0", "6.0", "10.0", "999999"};
        //String[] vqsIndex = new String[]{"0", "0.05", "0.1", "0.3", "0.5", "1.1566", "2.0", "3.0", "6.0", "10.0", "999999"};
        double[] vqDistribution = {
            35,      //0
            0,      //0.05
            0,      //0.5
            0,
            0,
            65,
            0,
            0,
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
        f.setVqsIndex(vqsIndex);
        f.setVqs(vqDistribution);
        
        List<Compartment> results = f.compute();
        
        for (Compartment r : results) {
            System.out.printf("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f\n", r.getVQ(), r.getQ(), r.getPH(), r.getPO2(), r.getPCO2(), r.getVN2(), r.getCaO2(), r.getCaCO2(), r.getR() );
        }
    }
    
    @Test
    public void testNaNPO2andPCO2() throws Exception{
        //vqsIndex=[0,0.05,0.1,0.3,0.5,0.9592671184736576,2.0,3.0,6.0,10.0,999999,]
        
        Vo2Formula f = new Vo2Formula();
        
        String[] vqsIndex = new String[]{"0", "0.05", "0.1", "0.3", "0.5", "0.9592671184736576", "2.0", "3.0", "6.0", "10.0", "999999"};
        double[] vqDistribution = {
            40,      //0
            0,      //0.05
            0,      //0.5
            0,
            0,
            60,
            0,
            0,
            0,
            0,
            15};
        
        double vO2 = 250;
        double vCO2 = 200;
        double cardiacOutput = 5;
        double hgb = 15;
        double bt = 37;
        double bp = 760;
        double fiO2 = 0.3; 
        
        f.setVo2(vO2);
        f.setVco2(vCO2);
        f.setCo(cardiacOutput);
        f.setHgb(hgb);
        f.setTemp(bt);
        f.setPb(bp);
        f.setFiO2(fiO2);
        f.setVqsIndex(vqsIndex);
        f.setVqs(vqDistribution);
        
        List<Compartment> results = f.compute();
        
        for (Compartment r : results) {
            System.out.printf("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f\n", r.getVQ(), r.getQ(), r.getPH(), r.getPO2(), r.getPCO2(), r.getVN2(), r.getCaO2(), r.getCaCO2(), r.getR() );
        }
    }
    
    
    
}
