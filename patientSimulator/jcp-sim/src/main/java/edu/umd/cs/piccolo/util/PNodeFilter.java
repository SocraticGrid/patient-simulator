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

import edu.umd.cs.piccolo.PNode;

/**
 * <b>PNodeFilter</b> is a interface that filters (accepts or rejects) nodes. Its
 * main use is to retrieve all the children of a node the meet some criteria
 * by using the method PNode.getAllNodes(collection, filter);
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public interface PNodeFilter {

	/**
	 * Return true if the filter should accept the given node.
	 */ 
	public boolean accept(PNode aNode); 
	
	/**
	 * Return true if the filter should test the children of
	 * the given node for acceptance.
	 */
	public boolean acceptChildrenOf(PNode aNode);
}
