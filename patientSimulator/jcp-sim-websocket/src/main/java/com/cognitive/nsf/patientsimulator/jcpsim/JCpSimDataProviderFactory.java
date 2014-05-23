/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitive.nsf.patientsimulator.jcpsim;

import org.jcpsim.data.JCpSimDataProvider;

/**
 *
 * @author esteban
 */
public interface JCpSimDataProviderFactory {
    public JCpSimDataProvider createProvider();
}
