/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.clock;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class DefaultClockImpl implements Clock {

    private List<ClockEventListener> listeners = new CopyOnWriteArrayList<ClockEventListener>();

    public void addClockChangeListener(ClockEventListener listener) {
        this.listeners.add(listener);
    }
    
    protected void notifyListeners(final ClockEvent event){
        for (ClockEventListener clockEventListener : listeners) {
            clockEventListener.onEvent(event);
        }
    }

}
