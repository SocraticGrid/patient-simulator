/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.patientsimulator.jcpsim;

import org.jcpsim.data.JCpSimData;

/**
 *
 * @author esteban
 */
public interface JCpSimDataReceivedEventListener {
    public void onDataReceived(JCpSimData data);
}
