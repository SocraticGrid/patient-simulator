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
package edu.umd.cs.piccolo.activities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * <b>PActivityScheduler</b> is responsible for maintaining a list of
 * activities. It is given a chance to process these activities from 
 * the PRoot's processInputs() method. Most users will not need to use
 * the PActivityScheduler directly, instead you should look at:
 * <ul>
 * <li>PNode.addActivity - to schedule a new activity 		
 * <li>PActivity.terminate - to terminate a running activity 		
 * <li>PRoot.processInputs - already calls processActivities for you.
 * </ul>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PActivityScheduler {
		
	private PRoot root;
	private List activities;
	private Timer activityTimer;
	private boolean activitiesChanged;
	private boolean animating;	
	private ArrayList processingActivities;

	public PActivityScheduler(PRoot rootNode) {
		root = rootNode;
		activities = new ArrayList();
		processingActivities = new ArrayList();
	}
		
	public PRoot getRoot() {
		return root;
	}
	
	public void addActivity(PActivity activity) {
		addActivity(activity, false);
	}

	/**
	 * Add this activity to the scheduler. Sometimes it's useful to make sure
	 * that an activity is run after all other activities have been run. To do
	 * this set processLast to true when adding the activity.
	 */
	public void addActivity(PActivity activity, boolean processLast) {
		if (activities.contains(activity)) return;

		activitiesChanged = true;
		
		if (processLast) {
			activities.add(0, activity);
		} else {
			activities.add(activity);
		}

		activity.setActivityScheduler(this);

		if (!getActivityTimer().isRunning()) {
			startActivityTimer();
		}		
	}
		
	public void removeActivity(PActivity activity) {
		if (!activities.contains(activity)) return;

		activitiesChanged = true;
		activities.remove(activity);

		if (activities.size() == 0) {
			stopActivityTimer();
		}					
	}

	public void removeAllActivities() {		
		activitiesChanged = true;	
		activities.clear();
		stopActivityTimer();
	}

	public List getActivitiesReference() {
		return activities;
	}
	
	/**
	 * Process all scheduled activities for the given time. Each activity
	 * is given one "step", equivalent to one frame of animation.
	 */	
	public void processActivities(long currentTime) {
		int size = activities.size();		
		if (size > 0) {
			processingActivities.clear();
			processingActivities.addAll(activities);
			for (int i = size - 1; i >= 0; i--) {
				PActivity each = (PActivity) processingActivities.get(i);
				each.processStep(currentTime);
			}
		}		
	}
		
	/**
	 * Return true if any of the scheduled activities return true to
	 * the message isAnimation();
	 */
	public boolean getAnimating() {
		if (activitiesChanged) {
			animating = false;
			for(int i=0; i<activities.size(); i++) {
				PActivity each = (PActivity) activities.get(i);
				animating |= each.isAnimation();
			}	
			activitiesChanged = false;
		}
		return animating;
	}
			
	protected void startActivityTimer() {
		getActivityTimer().start();
	}
	
	protected void stopActivityTimer() {
		getActivityTimer().stop();
	}
		
	protected Timer getActivityTimer() {
		if (activityTimer == null) {
			activityTimer = root.createTimer(PUtil.ACTIVITY_SCHEDULER_FRAME_DELAY, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					root.processInputs();
				}
			});
		}
		return activityTimer;
	}
}

