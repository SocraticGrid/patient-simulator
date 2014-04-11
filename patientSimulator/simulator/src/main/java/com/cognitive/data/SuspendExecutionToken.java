/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.data;


public class SuspendExecutionToken implements InternalToken {
    public boolean isAutoRetractable() {
        return false;
    }
}
