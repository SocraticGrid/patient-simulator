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

import java.awt.geom.AffineTransform;

import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * <b>PTransformActivity</b> interpolates between two transforms setting its
 * target's transform as it goes. See PNode. animate*() for an example of this
 * activity in used. The source transform is retrieved from the target just
 * before the animation is scheduled to start.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PTransformActivity extends PInterpolatingActivity {

	private static PAffineTransform STATIC_TRANSFORM = new PAffineTransform();

	private double[] source;
	private double[] destination;
	private Target target;

	/**
	 * <b>Target</b> Objects that want to get transformed by the transform 
	 * activity must implement this interface. See PNode.animateToTransform() 
	 * for one way to do this.
	 */
	public interface Target {
		
		/**
		 * This will be called by the transform activity for each new transform
		 * that it computes while it is stepping.
		 */
		public void setTransform(AffineTransform aTransform);
		
		/**
		 * This method is called right before the transform activity starts. That
		 * way an object is always animated from its current position.
		 */
		public void getSourceMatrix(double[] aSource);
	}

	public PTransformActivity(long duration, long stepRate, Target aTarget) {
		this(duration, stepRate, aTarget, null);
	}
	
	public PTransformActivity(long duration, long stepRate, Target aTarget, AffineTransform aDestination) {
		this(duration, stepRate, 1, PInterpolatingActivity.SOURCE_TO_DESTINATION, aTarget, aDestination);
	}

	/**
	 * Create a new PTransformActivity.
	 * <P>
	 * @param duration the length of one loop of the activity
	 * @param stepRate the amount of time between steps of the activity
	 * @param loopCount number of times the activity should reschedule itself
	 * @param mode defines how the activity interpolates between states
	 * @param aTarget the object that the activity will be applied to and where
	 * the source state will be taken from.
	 * @param aDestination the destination color state
	 */
	public PTransformActivity(long duration, long stepRate, int loopCount, int mode, Target aTarget, AffineTransform aDestination) {
		super(duration, stepRate, loopCount, mode);
		source = new double[6];
		destination = new double[6];
		target = aTarget;
		if (aDestination != null) aDestination.getMatrix(destination);
	}
	
	protected boolean isAnimation() {
		return true;
	}

	/**
	 * Return the final transform that will be set on the transform activities
	 * target when the transform activity stops stepping.
	 */
	public double[] getDestinationTransform() {
		return destination;
	}

	/**
	 * Set the final transform that will be set on the transform activities
	 * target when the transform activity stops stepping.
	 */
	public void setDestinationTransform(double[] newDestination) {
		destination = newDestination;
	}
	
	protected void activityStarted() { 
		if (getFirstLoop()) target.getSourceMatrix(source);
		super.activityStarted();
	}
	
	public void setRelativeTargetValue(float zeroToOne) {
		super.setRelativeTargetValue(zeroToOne);

		STATIC_TRANSFORM.setTransform(source[0] + (zeroToOne * (destination[0] - source[0])),
									  source[1] + (zeroToOne * (destination[1] - source[1])),
									  source[2] + (zeroToOne * (destination[2] - source[2])),
									  source[3] + (zeroToOne * (destination[3] - source[3])),
									  source[4] + (zeroToOne * (destination[4] - source[4])),
									  source[5] + (zeroToOne * (destination[5] - source[5])));
									  
		target.setTransform(STATIC_TRANSFORM);
	}
	
	//****************************************************************
	// Debugging - methods for debugging
	//****************************************************************

	/**
	 * Returns a string representing the state of this activity. This method is
	 * intended to be used only for debugging purposes, and the content and
	 * format of the returned string may vary between implementations. The
	 * returned string may be empty but may not be <code>null</code>.
	 *
	 * @return  a string representation of this activity's state
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
