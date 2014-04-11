/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.action;

import org.openide.nodes.Node;

/**
 *
 * @author esteban
 */
public interface SimulationPauseResumeCookie extends Node.Cookie{
    public void pauseResume();

    public void takeSnapshot();
}
