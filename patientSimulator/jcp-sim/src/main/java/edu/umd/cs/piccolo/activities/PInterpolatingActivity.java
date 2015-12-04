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

/**
 * <b>PInterpolatingActivity</b> interpolates between two states (source and
 * destination) over the duration of the activity. The interpolation can be
 * either linear or slow- in, slow-out.
 * <P>
 * The mode determines how the activity interpolates between the two states. The
 * default mode interpolates from source to destination, but you can also go
 * from destination to source, and from source to destination to source.
 * <P>
 * A loopCount of greater then one will make the activity reschedule itself when
 * it has finished. This makes the activity loop between the two states.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PInterpolatingActivity extends PActivity {

	public static final int SOURCE_TO_DESTINATION = 1;
	public static final int DESTINATION_TO_SOURCE = 2;
	public static final int SOURCE_TO_DESTINATION_TO_SOURCE = 3;

	private int mode;
	private boolean slowInSlowOut;
	private int loopCount;
	private boolean firstLoop;
	
	public PInterpolatingActivity(long duration, long stepRate) {
		this(duration, stepRate, 1, PInterpolatingActivity.SOURCE_TO_DESTINATION);
	}

	public PInterpolatingActivity(long duration, long stepRate, int loopCount, int mode) {
		this(duration, stepRate, System.currentTimeMillis(), loopCount, mode);
	}

	/**
	 * Create a new PInterpolatingActivity.
	 * <P>
	 * @param duration the length of one loop of the activity
	 * @param stepRate the amount of time between steps of the activity
	 * @param startTime the time (relative to System.currentTimeMillis()) that
	 * this activity should start.
	 * @param loopCount number of times the activity should reschedule itself
	 * @param mode defines how the activity interpolates between states
	 */
	public PInterpolatingActivity(long duration, long stepRate, long startTime, int loopCount, int mode) {
		super(duration, stepRate, startTime);
		this.loopCount = loopCount;
		this.mode = mode;
		slowInSlowOut = true;
		firstLoop = true;
	}
	
	/**
	 * Set the amount of time that this activity should take to complete,
	 * after the startStepping method is called. The duration must be greater
	 * then zero so that the interpolation value can be computed.
	 */
	public void setDuration(long aDuration) {
		if (aDuration <= 0) 
			throw new IllegalArgumentException("Duration for PInterpolatingActivity must be greater then 0");
		
		super.setDuration(aDuration);
	}
	
	//****************************************************************
	// Basics.
	//****************************************************************
	
	/**
	 * Return the mode that defines how the activity interpolates between
	 * states.
	 */
	public int getMode() {
		return mode;
	}
	
	/**
	 * Set the mode that defines how the activity interpolates between states.
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	/**
	 * Return the number of times the activity should automatically reschedule
	 * itself after it has finished.
	 */
	public int getLoopCount() {
		return loopCount;
	}

	/**
	 * Set the number of times the activity should automatically reschedule
	 * itself after it has finished.
	 */
	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	/**
	 * Return true if the activity is executing its first loop. Subclasses
	 * normally initialize their source state on the first loop.
	 */
	public boolean getFirstLoop() {
		return firstLoop;
	}

	/**
	 * Set if the activity is executing its first loop. Subclasses normally
	 * initialize their source state on the first loop. This method will rarely
	 * need to be called, unless your are reusing activities.
	 */
	public void setFirstLoop(boolean firstLoop) {
		this.firstLoop = firstLoop;
	}
		
	public boolean getSlowInSlowOut() {
		return slowInSlowOut;
	}

	public void setSlowInSlowOut(boolean isSlowInSlowOut) {
		slowInSlowOut = isSlowInSlowOut;
	}
			
	//****************************************************************
	// Stepping - Instead of overriding the step methods subclasses
	// of this activity will normally override setRelativeTargetValue().
	// This method will be called for every step of the activity with
	// a value ranging from 0,0 (for the first step) to 1.0 (for the 
	// final step). See PTransformActivity for an example.
	//****************************************************************
	
	protected void activityStarted() {
		super.activityStarted();
		setRelativeTargetValueAdjustingForMode(0);
	}
		
	protected void activityStep(long elapsedTime) { 
		super.activityStep(elapsedTime);

		float t = elapsedTime / (float) getDuration();
				
		t = Math.min(1, t);
		t = Math.max(0, t);
		
		if (getSlowInSlowOut()) {
			t = computeSlowInSlowOut(t);
		}
		
		setRelativeTargetValueAdjustingForMode(t);		
	}
	
	protected void activityFinished() {
		setRelativeTargetValueAdjustingForMode(1);
		super.activityFinished();
		
		PActivityScheduler scheduler = getActivityScheduler();
		if (loopCount > 1) {
			if (loopCount != Integer.MAX_VALUE) loopCount--;
			firstLoop = false;
			setStartTime(scheduler.getRoot().getGlobalTime());
			scheduler.addActivity(this);
		}
	}
	
	/**
	 * Stop this activity immediately, and remove it from the activity
	 * scheduler. If this activity is currently running then stoppedStepping
	 * will be called after it has been removed from the activity scheduler. 
	 */
	public void terminate() {
		loopCount = 0; // set to zero so that we don't reschedule self.
		super.terminate();
	}

	/**
	 * Subclasses should override this method and set the value on their
	 * target (the object that they are modifying) accordingly.
	 */
	public void setRelativeTargetValue(float zeroToOne) {
	}
	
	public float computeSlowInSlowOut(float zeroToOne) {
		if (zeroToOne < 0.5) {
			return 2.0f * zeroToOne * zeroToOne;
		} else {
			float complement = 1.0f - zeroToOne;
			return 1.0f - (2.0f * complement * complement);
		}
	}
	
	protected void setRelativeTargetValueAdjustingForMode(float zeroToOne) {
		switch (mode) {
			case SOURCE_TO_DESTINATION:
				break;

			case DESTINATION_TO_SOURCE:
				zeroToOne = 1 - zeroToOne;
				break;

			case SOURCE_TO_DESTINATION_TO_SOURCE:
				if (zeroToOne <= 0.5) {
					zeroToOne *= 2;
				} else {
					zeroToOne = 1 - ((zeroToOne - 0.5f) * 2);
				}
				break;
		}

		setRelativeTargetValue(zeroToOne);
	}
	
	//****************************************************************
	// Debugging - methods for debugging
	//****************************************************************
	
	/**
	 * Returns a string representing the state of this node. This method is
	 * intended to be used only for debugging purposes, and the content and
	 * format of the returned string may vary between implementations. The
	 * returned string may be empty but may not be <code>null</code>.
	 *
	 * @return  a string representation of this node's state
	 */
	protected String paramString() {
		StringBuffer result = new StringBuffer();

		if (slowInSlowOut) {
			result.append("slowinSlowOut,");			
		}
		
		result.append(super.paramString());

		return result.toString();
	}	
}
