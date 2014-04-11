/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.scenarios;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author esteban
 */
public class CustomRespiratorVitalSignsMonitor {

    private final int period;
    private final CustomRespirator respirator;
    private boolean started = false;
    private boolean modal = false;
    
    private AtomicBoolean isAlive = new AtomicBoolean(true);
    
    private Runnable task = new Runnable() {
        public void run() {
            if (!isAlive.get()){
                return;
            }
            
            boolean alive = true;

            if (respirator.getvPEEP() > 15) {
                alive = false;
            }


            if (!alive) {
                JOptionPane option = new JOptionPane("Patient is dead...", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = option.createDialog("RIP");
                dialog.setModal(modal);
                dialog.setVisible(true);
                isAlive.set(false);
            }

        }
    };

    public CustomRespiratorVitalSignsMonitor(CustomRespirator respirator, int period) {
        this.respirator = respirator;
        this.period = period;
    }

    public void start() {
        if (this.started) {
            throw new RuntimeException("Already running");
        }
        this.started = true;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(task, 0, period, TimeUnit.SECONDS);

    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }
}
