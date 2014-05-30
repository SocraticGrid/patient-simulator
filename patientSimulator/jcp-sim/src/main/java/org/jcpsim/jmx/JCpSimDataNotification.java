/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jcpsim.jmx;

import java.io.Serializable;
import javax.management.Notification;
import org.jcpsim.data.JCpSimData;

/**
 *
 * @author esteban
 */
public class JCpSimDataNotification extends Notification implements Serializable {
    
    private JCpSimData data;
    
    public JCpSimDataNotification(String type, Object source, long sequenceNumber) {
        super(type, source, sequenceNumber);
    }

    public JCpSimData getData() {
        return data;
    }

    public void setData(JCpSimData data) {
        this.data = data;
    }
    
    
}
