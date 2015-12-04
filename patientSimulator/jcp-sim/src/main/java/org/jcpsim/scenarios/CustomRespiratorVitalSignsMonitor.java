/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
