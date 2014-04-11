/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Build",
id = "com.cognitive.jcpsimsimulator.action.RunSimulationAction")
@ActionRegistration(
    iconBase = "com/cognitive/jcpsimsimulator/action/runProject.png",
displayName = "#CTL_RunSimulationAction")
@ActionReferences({
    @ActionReference(path = "Menu/RunProject", position = 0, separatorBefore = -50, separatorAfter = 50),
    @ActionReference(path = "Toolbars/Build", position = -20)
})
@Messages("CTL_RunSimulationAction=Run Simulation")
public final class RunSimulationAction implements ActionListener {

    private final SimulationRunCookie context;

    public RunSimulationAction(SimulationRunCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.runSimulation();
    }
}
