/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitive.bp.poc.model;

/**
 * Class used as a fact to notify UI clients about the request of a new Alert 
 * to AA.
 * @author esteban
 */
public class AlertRequest {
    private final String message;
    private final long delay;

    public AlertRequest(String message, long delay) {
        this.message = message;
        this.delay = delay;
    }

    public String getMessage() {
        return message;
    }

    public long getDelay() {
        return delay;
    }
    
}
