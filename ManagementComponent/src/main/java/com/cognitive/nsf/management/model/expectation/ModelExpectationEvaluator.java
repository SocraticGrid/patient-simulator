/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.model.expectation;

import com.cognitive.nsf.management.model.Model;
import org.jcpsim.data.JCpSimData;

/**
 *
 * @author esteban
 */
public interface ModelExpectationEvaluator  {
    public void processData(JCpSimData data);
    public void configure(PolicyEnforcer policyEnforcer);
    public Model getModel();
}
