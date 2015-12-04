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
package edu.umd.cs.piccolo.event;

import java.util.EventListener;

/**
 * <b>PInputEventListener</b> defines the most basic interface for objects that
 * want to listen to PNodes for input events. This interface is very simple so that
 * others may extend Piccolo's input management system. If you are just using Piccolo's
 * default input management system then you will most often use PBasicInputEventHandler
 * to register with a node for input events. 
 * <P>
 * @see PBasicInputEventHandler
 * @version 1.0
 * @author Jesse Grosjean
 */
public interface PInputEventListener extends EventListener {
	
	public void processEvent(PInputEvent aEvent, int type);

}
