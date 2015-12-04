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
package org.jcpsim.clock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SimulationClock extends DefaultClockImpl {
    
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private AtomicLong currentTime = new AtomicLong();
    private int interval = 10;
    
    private Semaphore pausedSemaphore = new Semaphore(1, true);
    
    private class ClockTick implements Runnable{

        public void run() {
            try {
                pausedSemaphore.acquire();
                currentTime.addAndGet(interval);
                pausedSemaphore.release();
                executorService.schedule(this, interval, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulationClock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private ClockTick tick = new ClockTick();
    
    public SimulationClock() {
        executorService.schedule(tick, interval, TimeUnit.MILLISECONDS);
    }

    public long getCurrentTime() {
        return this.currentTime.get();
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime.set(currentTime);
    }

    public void pause() {
        this.pause(true);
    }

    public void resume() {
        this.resume(true);
    }
    
    public void pause(boolean notify) {
        try {
            pausedSemaphore.acquire();
            if (notify){
                this.notifyListeners(new ClockEvent(ClockEvent.TYPE.CLOCK_PAUSED, currentTime.get()));
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SimulationClock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resume(boolean notify) {
        pausedSemaphore.release();
        
        if (notify){
            this.notifyListeners(new ClockEvent(ClockEvent.TYPE.CLOCK_RESUMED, currentTime.get()));
        }
    }
    
}
