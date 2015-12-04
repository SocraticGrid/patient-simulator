/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitive.bp.poc.model;

/**
 *
 * @author esteban
 */
public class ToggleService {
    
    public static enum SERVICE{
        ALERTS,
        GENOME
    }
    
    private final SERVICE service;
    private final boolean on;

    public ToggleService(SERVICE service, boolean on) {
        this.service = service;
        this.on = on;
    }

    public SERVICE getService() {
        return service;
    }

    public boolean isOn() {
        return on;
    }
    
}
