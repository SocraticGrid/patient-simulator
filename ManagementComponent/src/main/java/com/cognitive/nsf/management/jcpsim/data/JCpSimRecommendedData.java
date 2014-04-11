/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.jcpsim.data;

import com.cognitive.nsf.management.jcpsim.JCpSimDataUtils;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;

/**
 *
 * @author esteban
 */
public class JCpSimRecommendedData extends JCpSimDataImpl{
    
    public static JCpSimRecommendedData fromJCpSimData(JCpSimData data){
        JCpSimRecommendedData result = new JCpSimRecommendedData();
        
        JCpSimDataUtils.copy(data, result);
        
        return result;
    }
}
