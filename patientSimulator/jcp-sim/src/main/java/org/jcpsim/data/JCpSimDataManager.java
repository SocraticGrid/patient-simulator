/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.data;

/**
 *
 * @author esteban
 */
public interface JCpSimDataManager extends JCpSimDataProvider, JCpSimDataWriter {

    public void requestPause();
    public void resume();
    
}
