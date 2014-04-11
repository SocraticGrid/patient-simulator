/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.jcpsim;

import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimDataImpl;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class JCpSimDataUtils {
    
    public static JCpSimData cloneJCpSimData(JCpSimData original){
        JCpSimDataImpl copy = new JCpSimDataImpl();

        JCpSimDataUtils.copy(original, copy);
        
        return copy;
    }
    
    public static void copy(JCpSimData source, JCpSimData target){
        
        for (JCpSimParameter p : JCpSimParameter.values()) {
            target.set(p, source.get(p));
        }
        
    }
    
}
