/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.gui;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.jcpsim.clock.ClockEvent;
import org.jcpsim.clock.ClockEventListener;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;
import org.jcpsim.jmx.JCpSimCustomRespiratorMgmt;
import org.jcpsim.jmx.JCpSimTopMenuMgmt;
import org.jcpsim.run.Global;
import org.jcpsim.scenarios.ArterialLine;
import org.jcpsim.scenarios.CustomRespirator;

/**
 *
 * @author esteban
 */
public class TopMenu extends javax.swing.JPanel implements ClockEventListener{

    public final static String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:9997/JCpSim";
    private final static int JMX_PORT = 9997;
    
    private CustomRespirator mainRespirator;
    private CustomRespirator auxRespirator;
    
    private ArterialLine arterialLine;
    
    private JCpSimTopMenuMgmt mgmntMbean;
    
    /**
     * Creates new form TopMenu
     */
    public TopMenu() {
        initComponents();
        registerMBean();
        
    }

    public void registerRespirator(Global.MODE mode, CustomRespirator respirator){
        switch (mode){
            case AUX:
                this.auxRespirator = respirator;
                break;
            case SIM:
                this.mainRespirator = respirator;
                respirator.getClock().addClockChangeListener(this);
                break;
        }
        
    }
    public void registerArterialLine(Global.MODE mode, ArterialLine arterialLine){
        switch (mode){
            case SIM:
                this.arterialLine = arterialLine;
                arterialLine.getClock().addClockChangeListener(this);
                break;
            default:
                throw new RuntimeException("MODE '"+mode+"' not supported in ArterialLine");
        }
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnPause = new javax.swing.JToggleButton();
        btnRequestPause = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        cboSimulationId = new javax.swing.JComboBox();
        btnTakeSnapshot = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();

        jToolBar1.setRollover(true);

        btnPause.setText("Pause");
        btnPause.setFocusable(false);
        btnPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPause);

        btnRequestPause.setText("Request Pause");
        btnRequestPause.setFocusable(false);
        btnRequestPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRequestPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRequestPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestPauseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRequestPause);
        jToolBar1.add(jSeparator1);

        cboSimulationId.setMaximumSize(new java.awt.Dimension(200, 32767));
        cboSimulationId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSimulationIdActionPerformed(evt);
            }
        });
        jToolBar1.add(cboSimulationId);

        btnTakeSnapshot.setText("Take Snapshot");
        btnTakeSnapshot.setEnabled(false);
        btnTakeSnapshot.setFocusable(false);
        btnTakeSnapshot.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTakeSnapshot.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTakeSnapshot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTakeSnapshotActionPerformed(evt);
            }
        });
        jToolBar1.add(btnTakeSnapshot);
        jToolBar1.add(jSeparator2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        if (btnPause.isSelected()){
            if (this.mainRespirator != null){
                this.mainRespirator.getClock().pause();
            }
            if (this.arterialLine != null){
                this.arterialLine.getClock().pause();
            }
        } else{
            if (this.mainRespirator != null){
                this.mainRespirator.resume();
            }
            if (this.arterialLine != null){
                this.arterialLine.getClock().resume();
            }
        }
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnRequestPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestPauseActionPerformed
        if (this.mainRespirator != null){
            this.mainRespirator.requestPause();
        }
        if (this.arterialLine != null){
            this.arterialLine.requestPause();
        }
        this.btnRequestPause.setText("Pause Requested");
        this.btnRequestPause.setEnabled(false);
    }//GEN-LAST:event_btnRequestPauseActionPerformed

    private void cboSimulationIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSimulationIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSimulationIdActionPerformed

    private void btnTakeSnapshotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTakeSnapshotActionPerformed
        this.mgmntMbean.requestSnapshot(this.cboSimulationId.getSelectedItem().toString());
    }//GEN-LAST:event_btnTakeSnapshotActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnPause;
    private javax.swing.JButton btnRequestPause;
    private javax.swing.JButton btnTakeSnapshot;
    private javax.swing.JComboBox cboSimulationId;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    public void onEvent(ClockEvent event) {
        switch (event.getType()){
            case CLOCK_PAUSED:
                this.btnTakeSnapshot.setEnabled(this.cboSimulationId.getItemCount() > 0);
                this.btnPause.setSelected(true);
                this.btnPause.setText("Resume");
                this.btnRequestPause.setText("Request Pause");
                this.btnRequestPause.setEnabled(false);
                
                //copy all parameters from main to aux
                if (auxRespirator != null){
                    copyParameters(mainRespirator, auxRespirator);
                }
                
                break;
            case CLOCK_RESUMED:
                this.btnTakeSnapshot.setEnabled(false);
                this.btnPause.setSelected(false);
                this.btnPause.setText("Pause");
                this.btnRequestPause.setEnabled(true);
                
                //copy all parameters from aux to main
                if (auxRespirator != null){
                    copyParameters(auxRespirator, mainRespirator);
                }
                
                break;
        }
    }

    public void onSimulationStarted(String simulationId){
        this.cboSimulationId.addItem(simulationId);
        this.cboSimulationId.setSelectedItem(simulationId);
        
        
        if (mainRespirator != null){
            this.mainRespirator.resetCurrentSimulationTime();
        }
        if (auxRespirator != null){
            this.auxRespirator.resetCurrentSimulationTime();
        }
        if (arterialLine != null){
            this.arterialLine.resetCurrentSimulationTime();
        }
    }
    
    public void onSimulationStopped(String simulationId){
        this.cboSimulationId.removeItem(simulationId);
        if (mainRespirator != null){
            this.mainRespirator.stopCurrentSimulationTime();
        }
        if (auxRespirator != null){
            this.auxRespirator.stopCurrentSimulationTime();
        }
        if (arterialLine != null){
            this.arterialLine.stopCurrentSimulationTime();
        }
    }
    
    private void registerMBean() {
        try{
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            mgmntMbean = new JCpSimTopMenuMgmt(this);
            
            ObjectName mgmntMBeanName = new ObjectName(JCpSimTopMenuMgmt.OBJECT_NAME);
            
            mbs.registerMBean(mgmntMbean, mgmntMBeanName);
            
            java.rmi.registry.LocateRegistry.createRegistry(JMX_PORT);
            JMXServiceURL url = new JMXServiceURL(JMX_URL);
            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
            cs.start();
        } catch(Exception e){
           e.printStackTrace(); 
        }
    }
    
    private void copyParameters(CustomRespirator src, CustomRespirator target){
        
        JCpSimCustomRespiratorMgmt srcMgmt = new JCpSimCustomRespiratorMgmt(src);
        JCpSimCustomRespiratorMgmt targetMgmt = new JCpSimCustomRespiratorMgmt(target);
        
        JCpSimData srcData = srcMgmt.getData();
        for (JCpSimParameter parameter : JCpSimParameter.values()) {
            try{
                targetMgmt.set(parameter, srcData.get(parameter));
            } catch(Exception e){
                
            }
        }
        
    }
}
