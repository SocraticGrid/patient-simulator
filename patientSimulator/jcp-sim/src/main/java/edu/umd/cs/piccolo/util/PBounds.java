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
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * <b>PBounds</b> is simply a Rectangle2D.Double with extra methods that more
 * properly deal with the case when the rectangle is "empty".  A PBounds
 * has an extra bit to store emptiness.  In this state, adding new geometry
 * replaces the current geometry. A PBounds is emptied with the reset() method.
 * A useful side effect of the reset method is that it only modifies the fIsEmpty
 * variable, the other x, y, with, height variables are left alone. This is used
 * by Piccolo's layout management system to see if a the full bounds of a node
 * has really changed when it is recomputed. See PNode.validateLayout.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PBounds extends Rectangle2D.Double implements Serializable {

	private boolean isEmpty = true;

	public PBounds() {
		super();
	}

	public PBounds(PBounds aBounds) {
		this(aBounds.x, aBounds.y, aBounds.width, aBounds.height);
		isEmpty = aBounds.isEmpty();
	}

	public PBounds(Rectangle2D aBounds) {
		this(aBounds.getX(), aBounds.getY(), aBounds.getWidth(), aBounds.getHeight());
		isEmpty = aBounds.isEmpty();
	}

	public PBounds(Point2D aCenterPoint, double insetX, double insetY) {
		this(aCenterPoint.getX(), aCenterPoint.getY(), 0, 0);
		inset(insetX, insetY);
	}
	
	public PBounds(double x, double y, double width, double height) {
		super(x, y, width, height);
		isEmpty = false;
	}

	public Object clone() {
		return new PBounds(this);
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public PBounds reset() {
		isEmpty = true;
		return this;
	}

	public PBounds resetToZero() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
		isEmpty = true;
		return this;
	}

	public void setRect(Rectangle2D r) {
		super.setRect(r);
		isEmpty = false;
	}

	public void setRect(PBounds b) {
		isEmpty = b.isEmpty;
		x = b.x;
		y = b.y;
		width = b.width;
		height = b.height;
	}

	public void setRect(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		isEmpty = false;
	}

	public void add(double newx, double newy) {
		if (isEmpty) {
			setRect(newx, newy, 0, 0);
			isEmpty = false;
		} else {
			super.add(newx, newy);
		}
	}

	public void add(Rectangle2D r) {
		if (isEmpty) {
			setRect(r);
		} else {
			super.add(r);
		}
	}

	// optimized add when adding two PBounds together.
	public void add(PBounds r) {
		if (r.isEmpty) {
			return;
		} else if (isEmpty) {
			x = r.x;
			y = r.y;
			width = r.width;
			height = r.height;
			isEmpty = false;
		} else {
			double x1 = (x <= r.x) ? x : r.x;
			double y1 = (y <= r.y) ? y : r.y;
			double x2 = ((x + width) >= (r.x + r.width)) ? (x + width) : (r.x + r.width);			
			double y2 = ((y + height) >= (r.y + r.height)) ? (y + height) : (r.y + r.height);

			x = x1;
			y = y1;
			width = x2 - x1;
			height = y2 - y1;
			isEmpty = false;
		}
	}
	
	public Point2D getOrigin() {
		return new Point2D.Double(x, y);
	}

	public PBounds setOrigin(double x, double y) {
		this.x = x;
		this.y = y;
		isEmpty = false;
		return this;
	}

	public Dimension2D getSize() {
		return new PDimension(width, height);
	}
	
	public void setSize(double width, double height) {
		setRect(x, y, width, height);
	}

	public Point2D getCenter2D() {
		return new Point2D.Double(getCenterX(), getCenterY());
	}

	public PBounds moveBy(double dx, double dy) {
		setOrigin(x + dx, y + dy);
		return this;
	}

	public void expandNearestIntegerDimensions() {
		x = Math.floor(x);
		y = Math.floor(y);
		width = Math.ceil(width);
		height = Math.ceil(height);
	}
	
	public PBounds inset(double dx, double dy) {
		setRect(x + dx,
				y + dy,
				width - (dx*2),
				height - (dy*2));
		return this;
	}

	public PDimension deltaRequiredToCenter(Rectangle2D b) {
		PDimension result = new PDimension();
		double xDelta = getCenterX() - b.getCenterX();
		double yDelta = getCenterY() - b.getCenterY();
		result.setSize(xDelta, yDelta);		
		return result;
	}
	
	public PDimension deltaRequiredToContain(Rectangle2D b) {
		PDimension result = new PDimension();
		
		if (!contains(b)) {
			double bMaxX = b.getMaxX();
			double bMinX = b.getMinX();
			double bMaxY = b.getMaxY();
			double bMinY = b.getMinY();
			double maxX = getMaxX();
			double minX = getMinX();
			double maxY = getMaxY();
			double minY = getMinY();

			if (!(bMaxX > maxX && bMinX < minX)) {
				if (bMaxX > maxX || bMinX < minX) {
					double difMaxX = bMaxX - maxX;
					double difMinX = bMinX - minX;
					if (Math.abs(difMaxX) < Math.abs(difMinX)) {
						result.width = difMaxX;
					} else {
						result.width = difMinX;
					}
				}				
			}

			if (!(bMaxY > maxY && bMinY < minY)) {
				if (bMaxY > maxY || bMinY < minY) {
					double difMaxY = bMaxY - maxY;
					double difMinY = bMinY - minY;
					if (Math.abs(difMaxY) < Math.abs(difMinY)) {
						result.height = difMaxY;
					} else {
						result.height = difMinY;
					}
				}
			}
		}
		
		return result;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(width);
		out.writeDouble(height);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		x = in.readDouble();
		y = in.readDouble();
		width = in.readDouble();
		height = in.readDouble();
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append(getClass().getName().replaceAll(".*\\.", ""));
		result.append('[');
		
		if (isEmpty) {
			result.append("EMPTY");
		} else {
			result.append("x=");
			result.append(x);
			result.append(",y=");
			result.append(y);
			result.append(",width=");
			result.append(width);
			result.append(",height=");
			result.append(height);
		}
		
		result.append(']');
		
		return result.toString();
	}	
}
