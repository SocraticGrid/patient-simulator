/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.data;

/**
 *
 * @author esteban
 */
public interface JCpSimDataProvider {
    public JCpSimData getData();
    
    /**
     * Helpful method to use with JConsole.
     * @return 
     */
    public String getDataAsString();
}
