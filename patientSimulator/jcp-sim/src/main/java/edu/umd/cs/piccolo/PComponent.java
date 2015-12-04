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

import java.awt.Cursor;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * Interface that a component needs to implement if it wants to act as a Piccolo
 * canvas.
 * 
 * @version 1.0
 * @author Lance Good
 */
public interface PComponent {
		
	public void repaint(PBounds bounds);
	
	public void paintImmediately();
	
	public void pushCursor(Cursor cursor);
	
	public void popCursor();

	public void setInteracting(boolean interacting);		
}
