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
package edu.umd.cs.piccolo;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PNodeFilter;
import org.jcpsim.clock.Clock;

/**
 * <b>PRoot</b> serves as the top node in Piccolo's runtime structure. 
 * The PRoot responsible for running the main UI loop that processes
 * input from activities and external events.
 * <P>
 * @version 1.1
 * @author Jesse Grosjean
 */
public class PRoot extends PNode {
	
	/** 
	 * The property name that identifies a change in the set of this root's
	 * input sources (see {@link InputSource InputSource}).  In any property
	 * change event the new value will be a reference to the list of this root's
	 * input sources, but old value will always be null.
	 */
	public static final String PROPERTY_INPUT_SOURCES = "inputSources";
    public static final int PROPERTY_CODE_INPUT_SOURCES = 1 << 14;

	protected transient boolean processingInputs;
	protected transient boolean processInputsScheduled;

	private PInputManager defaultInputManager;
	private transient List inputSources;
	private transient long globalTime;
	private PActivityScheduler activityScheduler;
        
        private final Clock clock;

	/**
	 * This interfaces is for advanced use only. If you want to implement a 
	 * different kind of input framework then Piccolo provides you can hook
	 * it in here.
	 */
	public static interface InputSource {
		public void processInput();
	}
				
	/**
	 * Construct a new PRoot(). Note the PCanvas already creates a basic scene
	 * graph for you so often you will not need to construct your own roots.
	 */
	public PRoot(Clock clock) {
		super();
                this.clock = clock;
		inputSources = new ArrayList();
		globalTime = clock.getCurrentTime();
		activityScheduler = new PActivityScheduler(this);
	}

	//****************************************************************
	// Activities
	//****************************************************************
	
	/**
	 * Add an activity to the activity scheduler associated with this root.
	 * Activities are given a chance to run during each call to the roots
	 * <code>processInputs</code> method. When the activity has finished
	 * running it will automatically get removed.
	 */
	public boolean addActivity(PActivity activity) {
		getActivityScheduler().addActivity(activity);
		return true;
	}
	
	/**
	 * Get the activity scheduler associated with this root.
	 */
	public PActivityScheduler getActivityScheduler() {
		return activityScheduler;
	}
	
	/**
	 * Wait for all scheduled activities to finish before returning from
	 * this method. This will freeze out user input, and so it is generally
	 * recommended that you use PActivities.setTriggerTime() to offset activities
	 * instead of using this method.
	 */
	public void waitForActivities() {
		PNodeFilter cameraWithCanvas = new PNodeFilter() {
			public boolean accept(PNode aNode) {
				return (aNode instanceof PCamera) && (((PCamera)aNode).getComponent() != null);
			}
			public boolean acceptChildrenOf(PNode aNode) {
				return true;
			}
		};
		
		while (activityScheduler.getActivitiesReference().size() > 0) {
			processInputs();
			Iterator i = getAllNodes(cameraWithCanvas, null).iterator();
			while (i.hasNext()) {
				PCamera each = (PCamera) i.next();
				each.getComponent().paintImmediately();
			}
		}	
	}

	//****************************************************************
	// Basics
	//****************************************************************
	
	/**
	 * Return this.
	 */
	public PRoot getRoot() {
		return this;
	}
	
	/**
	 * Get the default input manager to be used when processing input
	 * events. PCanvas's use this method when they forward new swing input
	 * events to the PInputManager.
	 */
	public PInputManager getDefaultInputManager() {
		if (defaultInputManager == null) {
			defaultInputManager = new PInputManager();
			addInputSource(defaultInputManager);			
		}
		return defaultInputManager;
	}
	
	/**
	 * Advanced. If you want to add additional input sources to the roots
	 * UI process you can do that here. You will seldom do this unless you
	 * are making additions to the piccolo framework.
	 */
	public void addInputSource(InputSource inputSource) {
		inputSources.add(inputSource);
		firePropertyChange(PROPERTY_CODE_INPUT_SOURCES ,PROPERTY_INPUT_SOURCES, null, inputSources); 			
	}
	
	/**
	 * Advanced. If you want to remove the default input source from the roots
	 * UI process you can do that here. You will seldom do this unless you
	 * are making additions to the piccolo framework.
	 */
	public void removeInputSource(InputSource inputSource) {
		inputSources.remove(inputSource);
		firePropertyChange(PROPERTY_CODE_INPUT_SOURCES ,PROPERTY_INPUT_SOURCES, null, inputSources); 			
	}

	/**
	 * Returns a new timer. This method allows subclasses, such as PSWTRoot to
	 * create custom timers that will be used transparently by the Piccolo
	 * framework.
	 */	
	public Timer createTimer(int delay, ActionListener listener) {
		return new Timer(delay,listener);
	}
	
	//****************************************************************
	// UI Loop - Methods for running the main UI loop of Piccolo. 
	//****************************************************************
	
	/**
	 * Get the global Piccolo time. This is set to System.currentTimeMillis() at
	 * the beginning of the roots <code>processInputs</code> method. Activities
	 * should usually use this global time instead of System.
	 * currentTimeMillis() so that multiple activities will be synchronized.
	 */
	public long getGlobalTime() {
		return globalTime;
	}
	
	/**
	 * This is the heartbeat of the Piccolo framework. Pending input events
	 * are processed. Activities are given a chance to run, and the bounds caches
	 * and any paint damage is validated.
	 */
	public void processInputs() {
                if (clock == null){
                    return;
                }
                
                PDebug.startProcessingInput();
		processingInputs = true;
		
		globalTime = clock.getCurrentTime();
		int count = inputSources == null ? 0: inputSources.size();
		for (int i = 0; i < count; i++) {
			InputSource each = (InputSource) inputSources.get(i);
			each.processInput();
		}
		
		activityScheduler.processActivities(globalTime);
		validateFullBounds();
		validateFullPaint();
		
		processingInputs = false;
		PDebug.endProcessingInput();
	}

	public void setFullBoundsInvalid(boolean fullLayoutInvalid) {
		super.setFullBoundsInvalid(fullLayoutInvalid);
		scheduleProcessInputsIfNeeded();
	}
	
	public void setChildBoundsInvalid(boolean childLayoutInvalid) {
		super.setChildBoundsInvalid(childLayoutInvalid);
		scheduleProcessInputsIfNeeded();
	}
	
	public void setPaintInvalid(boolean paintInvalid) {
		super.setPaintInvalid(paintInvalid);
		scheduleProcessInputsIfNeeded();	
	}
	
	public void setChildPaintInvalid(boolean childPaintInvalid) {
		super.setChildPaintInvalid(childPaintInvalid);
		scheduleProcessInputsIfNeeded();
	}
			
	public void scheduleProcessInputsIfNeeded() {
		// The reason for the special case here (when not in the event dispatch thread) is that
		// the SwingUtilitiles.invokeLater code below only invokes later with respect to the
		// event dispatch thread, it will invoke concurrently with other threads.
		if (!SwingUtilities.isEventDispatchThread()) {
			// Piccolo is not thread safe and should amost always be called from the 
			// Swing event dispatch thread. It should only reach this point when a new canvas
			// is being created.
			return;
		}
		
		PDebug.scheduleProcessInputs();
		
		if (!processInputsScheduled && !processingInputs && 
			(getFullBoundsInvalid() || getChildBoundsInvalid() || getPaintInvalid() || getChildPaintInvalid())) {
				
			processInputsScheduled = true;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					processInputs();
					PRoot.this.processInputsScheduled = false;
				}
			});
		}
	}

        
}
