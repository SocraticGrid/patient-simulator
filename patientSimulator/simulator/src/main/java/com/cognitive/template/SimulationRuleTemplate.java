/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.template;

/**
 *
 * @author esteban
 */
public interface SimulationRuleTemplate {

    String getImports();
    
    String getGlobalInitCode();

    String getGlobals();

    String getInitialInserts();

    String getRules();
    
}
