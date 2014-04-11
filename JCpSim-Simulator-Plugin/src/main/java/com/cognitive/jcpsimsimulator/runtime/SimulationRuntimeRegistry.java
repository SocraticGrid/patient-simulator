/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.runtime;

import com.cognitive.template.SimulationTemplateEngine;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.jcpsim.gui.TopMenu;
import org.jcpsim.jmx.JCpSimTopMenuMgmt;
import org.jcpsim.jmx.JCpSimTopMenuMgmtMBean;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;

/**
 *
 * @author esteban
 */
public class SimulationRuntimeRegistry {
    
    private static SimulationRuntimeRegistry INSTANCE = new SimulationRuntimeRegistry();
    
    private Map<String,SimulationTask> tasks = new HashMap<String, SimulationTask>();

    private SimulationRuntimeRegistry() {
    }
    
    public static SimulationRuntimeRegistry getInstance(){
        return INSTANCE;
    }
    
    public void startSimulation(final String simulationTaskId, final SimulationTemplateEngine engine, final SimulationTask.SimulationTaskListener completedListener, final Map<String, Object> globals){
        
        StatusDisplayer.getDefault().setStatusText("Creating simulation task...");
        try {
            final JCpSimTopMenuMgmtMBean topMenuMBeanReference = this.getTopMenuMBeanReference();
            ProgressUtils.showProgressDialogAndRun(new Runnable() {

            @Override
            public void run() {
                SimulationTask task = new SimulationTask(simulationTaskId, engine, topMenuMBeanReference, globals, completedListener);
                task.startInNewThread();
                tasks.put(simulationTaskId, task);
                StatusDisplayer.getDefault().setStatusText("");
            }
        }, "Creating simulation task...");
        } catch (InstanceNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MalformedObjectNameException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }
    
    public boolean isSimulationPaused(String simulationTaskId){
        return this.tasks.get(simulationTaskId).isPaused();
    }

    public void pauseSimulation(String simulationTaskId){
        this.tasks.get(simulationTaskId).requestPause();
    }
    
    public void resumeSimulation(String simulationTaskId){
        this.tasks.get(simulationTaskId).resume();
    }
    
    public void takeSnapshot(String simulationTaskId){
        this.tasks.get(simulationTaskId).takeSnapshot();
    }

    private JCpSimTopMenuMgmtMBean getTopMenuMBeanReference() throws MalformedURLException, IOException, MalformedObjectNameException, InstanceNotFoundException{
        //in order to suppport JCpSim disconnections, the JMXService
        //is retrieved each time this method is invoked.
        JMXServiceURL serviceUrl = new JMXServiceURL(TopMenu.JMX_URL);
        JMXConnector jmxc = JMXConnectorFactory.connect(serviceUrl, null);

        MBeanServerConnection connection = jmxc.getMBeanServerConnection();

        connection.addNotificationListener(new ObjectName(JCpSimTopMenuMgmt.OBJECT_NAME), new NotificationListener() {

            @Override
            public void handleNotification(Notification notification, Object handback) {
                
                String simulationId = notification.getMessage();
                takeSnapshot(simulationId);
                
            }
        }, null, null);
        
        JCpSimTopMenuMgmtMBean mbean = JMX.newMBeanProxy(connection, new ObjectName(JCpSimTopMenuMgmt.OBJECT_NAME),
                                                         JCpSimTopMenuMgmtMBean.class, true);
        
        return mbean;
    }
}
