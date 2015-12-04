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

import java.util.ArrayList;

/**
 * <b>PStack</b> this class should be removed when a non thread safe stack is added
 * to the java class libraries.
 * <p>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PStack extends ArrayList {
	
	public PStack() {
	}
	
	public void push(Object o) {
		add(o);
	}
	
	public Object peek() {
		int s = size();
		if (s == 0) {
			return null;
		} else {
			return get(s - 1);
		}
	}
	
	public Object pop() {
		return remove(size() - 1);
	}	
}

