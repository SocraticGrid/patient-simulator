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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

/** 
 * <b>PObjectOutputStream</b> is an extension of ObjectOutputStream to handle optional 
 * elements. This is similar to the concept of Java's "weak references", but applied to
 * object serialization rather than garbage collection. Here, PObjectOutputStream
 * provides a method, <code>writeConditionalObject</code>, which only
 * serializes the specified object to the stream if there is a strong reference (if it
 * has been written somewhere else using writeObject()) to that object elsewhere in the 
 * stream.
 * <p>
 * To discover strong references to objects, PObjectOutputStream uses a two-phase writing 
 * process. First, a "discovery" phase is used to find out what objects are about to 
 * be serialized. This works by effectively serializing the object graph to /dev/null, recording 
 * which objects are unconditionally written using the standard writeObject method. Then,
 * in the second "write" phase, ObjectOutputStream actually serializes the data
 * to the output stream. During this phase, calls to writeConditionalObject() will
 * only write the specified object if the object was found to be serialized during the
 * discovery stage. If the object was not recorded during the discovery stage, a an optional null 
 * (the default) is unconditionally written in place of the object. To skip writting out the
 * null use <code>writeConditionalObject(object, false)</code>
 * <p>
 * By careful implementation of readObject and writeObject methods, streams serialized using
 * PObjectOutputStream can be deserialized using the standard ObjectInputStream.
 * <p>
 * @version 1.0
 * @author Jon Meyer
 * @author Jesse Grosjean
 */
public class PObjectOutputStream extends ObjectOutputStream {

	private boolean writingRoot;
	private HashMap unconditionallyWritten;

	public static byte[] toByteArray(Object aRoot) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PObjectOutputStream zout = new PObjectOutputStream(out);
		zout.writeObjectTree(aRoot);
		return out.toByteArray();
	}
	
	public PObjectOutputStream(OutputStream out) throws IOException {
		super(out);
		unconditionallyWritten = new HashMap();
	}

	public void writeObjectTree(Object aRoot) throws IOException {
		writingRoot = true;
		recordUnconditionallyWritten(aRoot); // record pass
		writeObject(aRoot); 				 // write pass
		writingRoot = false;
	}

	public void writeConditionalObject(Object object) throws IOException {
		if (!writingRoot) {
			throw new RuntimeException("writeConditionalObject() may only be called when a root object has been written.");
		}
		
		if (unconditionallyWritten.containsKey(object)) {
			writeObject(object);
		} else {
			writeObject(null);
		}
	}

	public void reset() throws IOException {
		super.reset();
		unconditionallyWritten.clear();
	}

	protected void recordUnconditionallyWritten(Object aRoot) throws IOException {
		class ZMarkObjectOutputStream extends PObjectOutputStream {
			public ZMarkObjectOutputStream() throws IOException {
				super(PUtil.NULL_OUTPUT_STREAM);
				enableReplaceObject(true);
			}
			public Object replaceObject(Object object) {
				PObjectOutputStream.this.unconditionallyWritten.put(object, Boolean.TRUE);
				return object;
			}	
			public void writeConditionalObject(Object object) throws IOException {
			}				
		}
		new ZMarkObjectOutputStream().writeObject(aRoot);
	}
}
