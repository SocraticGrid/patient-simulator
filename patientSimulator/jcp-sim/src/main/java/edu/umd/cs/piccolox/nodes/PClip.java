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
package edu.umd.cs.piccolox.nodes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * <b>PClip</b> is a simple node that applies a clip before rendering or picking its
 * children. PClip is a subclass of PPath, the clip applies is the GeneralPath wrapped
 * by its super class. See piccolo/examples ClipExample.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean 
 */
public class PClip extends PPath {

	public PBounds computeFullBounds(PBounds dstBounds) {
		if (dstBounds == null) dstBounds = new PBounds();
		dstBounds.reset();
		dstBounds.add(getBoundsReference());
		localToParent(dstBounds);
		return dstBounds;
	}

	public void repaintFrom(PBounds localBounds, PNode childOrThis) {
		if (childOrThis != this) {
			Rectangle2D.intersect(getBoundsReference(), localBounds, localBounds);
			super.repaintFrom(localBounds, childOrThis);
		} else {
			super.repaintFrom(localBounds, childOrThis);
		}
	}

	protected void paint(PPaintContext paintContext) {
		Paint p = getPaint();			
		if (p != null) {
			Graphics2D g2 = paintContext.getGraphics();
			g2.setPaint(p);
			g2.fill(getPathReference());
		}
		paintContext.pushClip(getPathReference());
	}
	
	protected void paintAfterChildren(PPaintContext paintContext) {
		paintContext.popClip(getPathReference());
		if (getStroke() != null && getStrokePaint() != null) {
			Graphics2D g2 = paintContext.getGraphics();
			g2.setPaint(getStrokePaint());
			g2.setStroke(getStroke());
			g2.draw(getPathReference());
		}		
	}
	
	public boolean fullPick(PPickPath pickPath) {
		if (getPickable() && fullIntersects(pickPath.getPickBounds())) {
			pickPath.pushNode(this);
			pickPath.pushTransform(getTransformReference(false));
			
			if (pick(pickPath)) {
				return true;
			}
			
			if (getChildrenPickable() && getPathReference().intersects(pickPath.getPickBounds())) { 		
				int count = getChildrenCount();
				for (int i = count - 1; i >= 0; i--) {
					PNode each = getChild(i);
					if (each.fullPick(pickPath))
						return true;
				}				
			}

			if (pickAfterChildren(pickPath)) {
				return true;
			}

			pickPath.popTransform(getTransformReference(false));
			pickPath.popNode(this);
		}

		return false;
	}	
}
