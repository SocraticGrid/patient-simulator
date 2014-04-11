/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.clock;

/**addClockChangeListener
 *
 * @author esteban
 */
public interface Clock {
    public long getCurrentTime();
    public void setCurrentTime(long currentTime);
    public void pause();
    public void pause(boolean notify);
    public void resume();
    public void resume(boolean notify);
    
    public void addClockChangeListener(ClockEventListener listener);
}
