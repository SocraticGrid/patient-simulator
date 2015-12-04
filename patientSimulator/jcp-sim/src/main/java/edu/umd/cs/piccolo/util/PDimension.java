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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * <b>PDimension</b> this class should be removed once a concrete Dimension2D 
 * that supports doubles is added to java. 
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PDimension extends Dimension2D implements Serializable {

	public double width;
	public double height;

	public PDimension() {
		super();
	}

	public PDimension(Dimension2D aDimension) {
		this(aDimension.getWidth(), aDimension.getHeight());
	}
	
	public PDimension(double aWidth, double aHeight) {
		super();
		width = aWidth;
		height = aHeight;
	}

	public PDimension(Point2D p1, Point2D p2) {
		width = p2.getX() - p1.getX();
		height = p2.getY() - p1.getY();
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public void setSize(double aWidth, double aHeight) {
		width = aWidth;
		height = aHeight;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append(super.toString().replaceAll(".*\\.", ""));
		result.append('[');
		result.append("width=");
		result.append(width);
		result.append(",height=");
		result.append(height);
		result.append(']');

		return result.toString();
	}	
}
