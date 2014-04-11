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
id = "com.cognitive.jcpsimsimulator.action.TakeSnapshotAction")
@ActionRegistration(
    iconBase = "com/cognitive/jcpsimsimulator/action/snapshot.png",
displayName = "#CTL_TakeSnapshotAction")
@ActionReferences({
    @ActionReference(path = "Menu/RunProject", position = 100),
    @ActionReference(path = "Toolbars/Build", position = -18)
})
@Messages("CTL_TakeSnapshotAction=Take Snapshot")
public final class TakeSnapshotAction implements ActionListener {

    private final SimulationPauseResumeCookie context;

    public TakeSnapshotAction(SimulationPauseResumeCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.takeSnapshot();
    }
}
