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
package edu.umd.cs.piccolo.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

/**
 * <b>PDebug</b> is used to set framework wide debugging flags.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PDebug {
	
	public static boolean debugRegionManagement = false;	
	public static boolean debugPaintCalls = false;	
	public static boolean debugPrintFrameRate = false;	
	public static boolean debugPrintUsedMemory = false; 
	public static boolean debugBounds = false;
	public static boolean debugFullBounds = false;
	public static boolean debugThreads = false;
	public static int printResultsFrameRate = 10;
	
	private static int debugPaintColor;

	private static long framesProcessed;
	private static long startProcessingOutputTime;
	private static long startProcessingInputTime;
	private static long processOutputTime;
	private static long processInputTime;
	private static boolean processingOutput;

	private PDebug() {
		super();
	}
	
	public static Color getDebugPaintColor() {
		int color = 100 + (debugPaintColor++ % 10) * 10;
		return new Color(color, color, color, 150);
	}
	
	// called when scene graph needs update.
	public static void scheduleProcessInputs() {
		if (debugThreads && !SwingUtilities.isEventDispatchThread()) {
			System.out.println("scene graph manipulated on wrong thread");
		}
	}
	
	public static void processRepaint() {
		if (processingOutput && debugPaintCalls) {
			System.err.println("Got repaint while painting scene. This can result in a recursive process that degrades performance.");
		}
		
		if (debugThreads && !SwingUtilities.isEventDispatchThread()) {
			System.out.println("repaint called on wrong thread");
		}
	}
	
	public static boolean getProcessingOutput() {
		return processingOutput;
	}
	
	public static void startProcessingOutput() {
		processingOutput = true;
		startProcessingOutputTime = System.currentTimeMillis();
	}
	
	public static void endProcessingOutput(Graphics g) {
		processOutputTime += (System.currentTimeMillis() - startProcessingOutputTime);
		framesProcessed++;
				
		if (PDebug.debugPrintFrameRate) {
			if (framesProcessed % printResultsFrameRate == 0) {
				System.out.println("Process output frame rate: " + getOutputFPS() + " fps");
				System.out.println("Process input frame rate: " + getInputFPS() + " fps");
				System.out.println("Total frame rate: " + getTotalFPS() + " fps");
				System.out.println();				
				resetFPSTiming();				
			}
		}
		
		if (PDebug.debugPrintUsedMemory) {
			if (framesProcessed % printResultsFrameRate == 0) { 		
				System.out.println("Approximate used memory: " + getApproximateUsedMemory() / 1024 + " k");
			}
		}
		
		if (PDebug.debugRegionManagement) {
			Graphics2D g2 = (Graphics2D)g;
			g.setColor(PDebug.getDebugPaintColor());
			g2.fill(g.getClipBounds().getBounds2D());
		}
		
		processingOutput = false;
	}

	public static void startProcessingInput() {
		startProcessingInputTime = System.currentTimeMillis();
	}
	
	public static void endProcessingInput() {
		processInputTime += (System.currentTimeMillis() - startProcessingInputTime);
	}
	
	/**
	 * Return how many frames are processed and painted per second. 
	 * Note that since piccolo doesn't paint continuously this rate
	 * will be slow unless you are interacting with the system or have
	 * activities scheduled.
	 */
	public static double getTotalFPS() {
		if ((framesProcessed > 0)) {
			return 1000.0 / ((processInputTime + processOutputTime) / (double) framesProcessed);
		} else {
			return 0;
		}
	}

	/**
	 * Return the frames per second used to process 
	 * input events and activities.
	 */
	public static double getInputFPS() {
		if ((processInputTime > 0) && (framesProcessed > 0)) {
			return 1000.0 / (processInputTime / (double) framesProcessed);
		} else {
			return 0;
		}
	}
	
	/**
	 * Return the frames per seconds used to paint
	 * graphics to the screen.
	 */
	public static double getOutputFPS() {
		if ((processOutputTime > 0) && (framesProcessed > 0)) {
			return 1000.0 / (processOutputTime / (double) framesProcessed);
		} else {
			return 0;
		}
	}
	
	/**
	 * Return the number of frames that have been processed since the last
	 * time resetFPSTiming was called.
	 */
	public long getFramesProcessed() {
		return framesProcessed;
	}
	
	/**
	 * Reset the variables used to track FPS. If you reset seldom they you will
	 * get good average FPS values, if you reset more often only the frames recorded
	 * after the last reset will be taken into consideration.
	 */
	public static void resetFPSTiming() {
		framesProcessed = 0;
		processInputTime = 0;
		processOutputTime = 0;
	}
	
	public static long getApproximateUsedMemory() {
		System.gc();
		System.runFinalization();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return totalMemory - free;
	}	
}
