/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.clock;


public class RealTimeClock extends DefaultClockImpl {

    private boolean paused;
    private long lastTime;
    
    public long getCurrentTime() {
        
        if (paused){
            return lastTime;
        }
        
        lastTime = System.currentTimeMillis();
        return lastTime;
    }

    public void setCurrentTime(long currentTime) {
    }

    public void pause() {
        this.pause(true);
    }

    public void resume() {
        this.resume(true);
    }

    public void pause(boolean notify) {
        this.paused = true;
        if (notify){
            this.notifyListeners(new ClockEvent(ClockEvent.TYPE.CLOCK_PAUSED, lastTime));
        }
    }

    public void resume(boolean notify) {
        this.paused = false;
        if (notify){
            this.notifyListeners(new ClockEvent(ClockEvent.TYPE.CLOCK_RESUMED, lastTime));
        }
    }
    
    
}
