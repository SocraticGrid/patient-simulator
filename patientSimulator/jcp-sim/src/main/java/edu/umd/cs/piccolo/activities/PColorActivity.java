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

import java.awt.Color;

/**
 * <b>PColorActivity</b> interpolates between two colors for its target over the
 * duration of the animation. The source color is retrieved from the target just
 * before the activity is scheduled to start.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PColorActivity extends PInterpolatingActivity {
	
	private Color source;
	private Color destination;
	private Target target;

	/**
	 * <b>Target</b> Objects that want their color to be set by the color
	 * activity must implement this interface.
	 */
	public interface Target {

		/**
		 * This will be called by the color activity for each new
		 * interpolated color that it computes while it is stepping.
		 */
		public void setColor(Color color);

		/**
		 * This method is called right before the color activity starts. That
		 * way an object's color is always animated from its current color the
		 * the destination color that is specified in the color activity.
		 */
		public Color getColor();
	}

	public PColorActivity(long duration, long stepRate, Target aTarget) {
		this(duration, stepRate, aTarget, null);
	}

	public PColorActivity(long duration, long stepRate, Target aTarget, Color aDestination) {
		this(duration, stepRate, 1, PInterpolatingActivity.SOURCE_TO_DESTINATION, aTarget, aDestination);
	}
	
	/**
	 * Create a new PColorActivity.
	 * <P>
	 * @param duration the length of one loop of the activity
	 * @param stepRate the amount of time between steps of the activity
	 * @param loopCount number of times the activity should reschedule itself
	 * @param mode defines how the activity interpolates between states
	 * @param aTarget the object that the activity will be applied to and where
	 * the source state will be taken from.
	 * @param aDestination the destination color state
	 */
	public PColorActivity(long duration, long stepRate, int loopCount, int mode, Target aTarget, Color aDestination) {
		super(duration, stepRate, loopCount, mode);
		target = aTarget;
		destination = aDestination;
	}	

	protected boolean isAnimation() {
		return true;
	}

	/**
	 * Return the final color that will be set on the color activities target
	 * when the activity stops stepping.
	 */
	public Color getDestinationColor() {
		return destination;
	}

	/**
	 * Set the final color that will be set on the color activities target when
	 * the activity stops stepping.
	 */
	public void setDestinationColor(Color newDestination) {
		destination = newDestination;
	}

	protected void activityStarted() {
		if (getFirstLoop()) source = target.getColor();
		super.activityStarted();
	}

	public void setRelativeTargetValue(float zeroToOne) {
		super.setRelativeTargetValue(zeroToOne);
		float red = (float) (source.getRed() + (zeroToOne * (destination.getRed() - source.getRed())));
		float green = (float) (source.getGreen() + (zeroToOne * (destination.getGreen() - source.getGreen())));
		float blue = (float) (source.getBlue() + (zeroToOne * (destination.getBlue() - source.getBlue())));
		float alpha = (float) (source.getAlpha() + (zeroToOne * (destination.getAlpha() - source.getAlpha())));
		target.setColor(new Color(red/255, green/255, blue/255, alpha/255));
	}

	//****************************************************************
	// Debugging - methods for debugging
	//****************************************************************

	/**
	 * Returns a string representing the state of this object. This method is
	 * intended to be used only for debugging purposes, and the content and
	 * format of the returned string may vary between implementations. The
	 * returned string may be empty but may not be <code>null</code>.
	 *
	 * @return  a string representation of this object's state
	 */
	protected String paramString() {
		StringBuffer result = new StringBuffer();

		result.append("source=" + (source == null ? "null" : source.toString()));
		result.append(",destination=" + (destination == null ? "null" : destination.toString()));
		result.append(',');
		result.append(super.paramString());

		return result.toString();
	}
}
