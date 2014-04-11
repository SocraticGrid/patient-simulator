/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator;

import com.cognitive.jcpsimsimulator.action.SimulationPauseResumeCookie;
import com.cognitive.jcpsimsimulator.action.SimulationRunCookie;
import com.cognitive.jcpsimsimulator.runtime.SimulationRuntimeRegistry;
import com.cognitive.jcpsimsimulator.runtime.SimulationTask;
import com.cognitive.template.FreeFormRuleTemplate;
import com.cognitive.template.SimulationTemplateEngine;
import com.cognitive.template.serialization.SimulationTemplateEngineSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_Simulation_LOADER=Files of Simulation"
})
@MIMEResolver.ExtensionRegistration(
    displayName = "#LBL_Simulation_LOADER",
mimeType = "text/jcpsim-sim",
extension = {"jsim"})
@DataObject.Registration(
    mimeType = "text/jcpsim-sim",
iconBase = "com/cognitive/jcpsimsimulator/chart.png",
displayName = "#LBL_Simulation_LOADER",
position = 300)
@ActionReferences({
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
    position = 100,
    separatorAfter = 200),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
    position = 300),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
    position = 400,
    separatorAfter = 500),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
    position = 600),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
    position = 700,
    separatorAfter = 800),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
    position = 900,
    separatorAfter = 1000),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
    position = 1100,
    separatorAfter = 1200),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
    position = 1300),
    @ActionReference(
        path = "Loaders/text/jcpsim-sim/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
    position = 1400)
})
public class SimulationDataObject extends MultiDataObject {

    private SimulationTemplateEngine engine;
    private EditorCookie editorCookie;
    private FreeFormRuleTemplate controllRules;

    private class SimulationRunImpl implements SimulationRunCookie {

        private boolean simulationRunning;
        
        @Override
        public void runSimulation() {
            getCookieSet().remove(this);
            getCookieSet().add(simulationPauseResume);
            simulationRunning = true;
            
            //set controll rules
            engine.addRuleTemplate(controllRules);
            //create globals
            Map<String, Object> globals = new HashMap<String, Object>();
            globals.put("simulationPauseResumeCookie", simulationPauseResume);
            
            SimulationRuntimeRegistry.getInstance().startSimulation(getName(), engine, new SimulationTask.SimulationTaskListener() {

                @Override
                public void onTaskCompleted() {
                    simulationRunning = false;
                    engine.getRuleTemplates().remove(controllRules);
                    getCookieSet().remove(simulationPauseResume);
                    getCookieSet().add(SimulationRunImpl.this);
                }

                @Override
                public void onError(Throwable t) {
                    simulationRunning = false;
                    engine.getRuleTemplates().remove(controllRules);
                    getCookieSet().remove(simulationPauseResume);
                    getCookieSet().add(SimulationRunImpl.this);
                }
            }, globals);
        }

        @Override
        public boolean isRunning() {
            return simulationRunning;
        }
    }
    
    private class SimulationPauseResumeImpl implements SimulationPauseResumeCookie{

        private boolean paused;
        
        @Override
        public void pauseResume() {
            if (!paused){
                SimulationRuntimeRegistry.getInstance().pauseSimulation(getName());
            }else{
                SimulationRuntimeRegistry.getInstance().resumeSimulation(getName());
            }
            paused = !paused;
        }

        @Override
        public void takeSnapshot() {
            SimulationRuntimeRegistry.getInstance().takeSnapshot(getName());
        }
        
    }
    
    private SimulationRunCookie simulationRun = new SimulationRunImpl();
    private SimulationPauseResumeCookie simulationPauseResume = new SimulationPauseResumeImpl();

    public SimulationDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        this.setEngine(this.parseData(pf.asText()));
        registerEditor("text/jcpsim-sim", true);
        
        CookieSet cookieSet = this.getCookieSet();
        editorCookie = cookieSet.getCookie(org.openide.cookies.EditorCookie.class);

        cookieSet.add(this.simulationRun);
        
        this.initializeControllRules();
    }
    
    
    
    @Override
    protected int associateLookup() {
        return 1;
    }

    @MultiViewElement.Registration(
        displayName = "#LBL_Simulation_EDITOR",
    iconBase = "com/cognitive/jcpsimsimulator/chart.png",
    mimeType = "text/jcpsim-sim",
    persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
    preferredID = "Simulation",
    position = 2000)
    @Messages("LBL_Simulation_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        MultiViewEditorElement multiViewEditorElement = new MultiViewEditorElement(lkp);
        return multiViewEditorElement;
    }

    public SimulationTemplateEngine getEngine() {
        return engine;
    }

    private SimulationTemplateEngine parseData(String content) {
        return SimulationTemplateEngineSerializer.getInstance().deserialize(content);
    }

    public void onEngineModifiedFromUI(SimulationTemplateEngine engine) {
        try {
            this.setEngine(engine);
            String data = SimulationTemplateEngineSerializer.getInstance().serialize(engine, true);
            editorCookie.getDocument().remove(0, editorCookie.getDocument().getLength());
            editorCookie.getDocument().insertString(0, data, null);
            
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void setModified(boolean modif) {
        super.setModified(modif);
        
    }

    private void setEngine(SimulationTemplateEngine engine) {
        SimulationTemplateEngine oldEngine = this.engine;
        this.engine = engine;
        this.firePropertyChange("engine", engine, oldEngine);
    }
    
    private void initializeControllRules() {
        this.controllRules = new FreeFormRuleTemplate();
        
        controllRules.addImport("import com.cognitive.jcpsimsimulator.action.SimulationPauseResumeCookie;");
        controllRules.addGlobal("global SimulationPauseResumeCookie simulationPauseResumeCookie;");
        
        StringBuilder builder = new StringBuilder();
        builder.append("rule \"Pause when value is modified\"\n");
        builder.append("    when ValueModifiedToken()\n");
        builder.append("then\n");
        builder.append("    simulationPauseResumeCookie.pauseResume();\n");
        builder.append("end\n");
        
        controllRules.setRules(builder.toString());
        
    }

    
}
