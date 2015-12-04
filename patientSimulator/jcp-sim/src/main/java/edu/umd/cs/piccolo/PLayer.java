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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PObjectOutputStream;

/**
 * <b>PLayer</b> is a node that can be viewed directly by multiple camera nodes.
 * Generally child nodes are added to a layer to give the viewing cameras 
 * something to look at.
 * <P>
 * A single layer node may be viewed through multiple cameras with each
 * camera using its own view transform. This means that any node (since layers can have
 * children) may be visible through multiple cameras at the same time.
 * <p>
 * @see PCamera
 * @see edu.umd.cs.piccolo.event.PInputEvent
 * @see edu.umd.cs.piccolo.util.PPickPath
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PLayer extends PNode {
	
	/** 
	 * The property name that identifies a change in the set of this layer's
	 * cameras (see {@link #getCamera getCamera}, {@link #getCameraCount
	 * getCameraCount}, {@link #getCamerasReference getCamerasReference}). In
	 * any property change event the new value will be a reference to the list
	 * of cameras, but old value will always be null.
	 */
	public static final String PROPERTY_CAMERAS = "cameras";
    public static final int PROPERTY_CODE_CAMERAS = 1 << 13;

	private transient List cameras;

	public PLayer() {
		super();
		cameras = new ArrayList();
	}
	
	//****************************************************************
	// Cameras - Maintain the list of cameras that are viewing this
	// layer.
	//****************************************************************
	
	/**
	 * Get the list of cameras viewing this layer.
	 */
	public List getCamerasReference() {
		return cameras;
	}

	/**
	 * Get the number of cameras viewing this layer.
	 */ 
	public int getCameraCount() {
		if (cameras == null) {
			return 0;
		}
		return cameras.size();
	}
	
	/**
	 * Get the camera in this layer's camera list at the specified index.
	 */
	public PCamera getCamera(int index) {
		return (PCamera) cameras.get(index);
	}
	
	/**
	 * Add a camera to this layer's camera list. This method it called automatically
	 * when a layer is added to a camera.
	 */
	public void addCamera(PCamera camera) {
		addCamera(cameras.size(), camera);
	}
	
	/**
	 * Add a camera to this layer's camera list at the specified index. This 
	 * method it called automatically when a layer is added to a camera.
	 */
	public void addCamera(int index, PCamera camera) {
		cameras.add(index, camera);
		invalidatePaint();
		firePropertyChange(PROPERTY_CODE_CAMERAS, PROPERTY_CAMERAS, null, cameras);
	}

	/**
	 * Remove the camera from this layer's camera list.
	 */ 
	public PCamera removeCamera(PCamera camera) {
		return removeCamera(cameras.indexOf(camera));
	}
	
	/**
	 * Remove the camera at the given index from this layer's camera list.
	 */ 
	public PCamera removeCamera(int index) {
		PCamera result = (PCamera) cameras.remove(index);
		invalidatePaint();
		firePropertyChange(PROPERTY_CODE_CAMERAS, PROPERTY_CAMERAS, null, cameras);
		return result;
	}
		
	//****************************************************************
	// Camera Repaint Notifications - Layer nodes must forward their
	// repaints to each camera that is viewing them so that the camera
	// views will also get repainted.
	//****************************************************************
		
	/**
	 * Override repaints and forward them to the cameras that are
	 * viewing this layer.
	 */
	public void repaintFrom(PBounds localBounds, PNode childOrThis) {						
		if (childOrThis != this) {
			localToParent(localBounds);
		}
			
		notifyCameras(localBounds);
		
		if (getParent() != null) {
			getParent().repaintFrom(localBounds, childOrThis);
		}
	}	
	
	protected void notifyCameras(PBounds parentBounds) {
		int count = getCameraCount();
		for (int i = 0; i < count; i++) {
			PCamera each = (PCamera) cameras.get(i);
			each.repaintFromLayer(parentBounds, this);
		}
	}
	
	//****************************************************************
	// Serialization - Layers conditionally serialize their cameras.
	// This means that only the camera references that were unconditionally
	// (using writeObject) serialized by someone else will be restored
	// when the layer is unserialized.
	//****************************************************************
	
	/**
	 * Write this layer and all its children out to the given stream. Note
	 * that the layer writes out any cameras that are viewing it conditionally, so they will only
	 * get written out if someone else writes them unconditionally.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		
		int count = getCameraCount();
		for (int i = 0; i < count; i++) {
			((PObjectOutputStream)out).writeConditionalObject(cameras.get(i));			
		}
		
		out.writeObject(Boolean.FALSE);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		cameras = new ArrayList();
		
		while (true) {
			Object each = in.readObject();
			if (each != null) {
				if (each.equals(Boolean.FALSE)) {
					break;
				} else {
					cameras.add(each);
				}
			}
		}
	}	
}
