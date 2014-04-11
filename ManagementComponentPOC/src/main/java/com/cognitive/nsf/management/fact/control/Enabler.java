/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.nsf.management.fact.control;

import java.util.Date;

/**
 *
 * @author esteban
 */
public class Enabler {
    private final String id;
    private Date lastActivationDate;

    public Enabler(String id, Date lastActivationDate) {
        this.id = id;
        this.lastActivationDate = lastActivationDate;
    }

    public String getId() {
        return id;
    }
    
    public Date getLastActivationDate() {
        return lastActivationDate;
    }

    public void setLastActivationDate(Date lastActivationDate) {
        this.lastActivationDate = lastActivationDate;
    }
    
}
