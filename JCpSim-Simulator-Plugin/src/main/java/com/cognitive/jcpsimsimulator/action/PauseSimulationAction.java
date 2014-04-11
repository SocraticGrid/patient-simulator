/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Actions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Build",
id = "com.cognitive.jcpsimsimulator.action.PauseSimulationAction")
@ActionRegistration(
    iconBase = "com/cognitive/jcpsimsimulator/action/pause.png",
displayName = "#CTL_PauseSimulationAction")
@ActionReferences({
    @ActionReference(path = "Menu/RunProject", position = -150),
    @ActionReference(path = "Toolbars/Build", position = -19)
})
@Messages("CTL_PauseSimulationAction=Pause Simulation")
public final class PauseSimulationAction extends AbstractAction {

    private final SimulationPauseResumeCookie context;

    public PauseSimulationAction(SimulationPauseResumeCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        this.context.pauseResume();
    }
    
    public static enum ICON{
        PAUSE("com/cognitive/jcpsimsimulator/action/pause.png"),
        RESUME("com/cognitive/jcpsimsimulator/action/resume.png");
        
        private String url;
        
        private ICON(String url){
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
        
    }
    
    public static void changeIcon(ICON icon){
        Action action = Actions.forID("Build", "com.cognitive.jcpsimsimulator.action.RunSimulationAction");
        ImageIcon imageIcon = ImageUtilities.loadImageIcon(icon.getUrl(), false);
        action.putValue(Action.SMALL_ICON, imageIcon);
    }

}
