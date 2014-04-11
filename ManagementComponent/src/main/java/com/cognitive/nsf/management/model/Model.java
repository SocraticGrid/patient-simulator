/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model;

import org.jcpsim.data.JCpSimData;

/**
 *
 * @author esteban
 */
public interface Model {
    public void init (ModelSessionManager manager);
    public void processData(JCpSimData data);
}
