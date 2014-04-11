/*
 * jCpSim - Java CardioPulmonary SIMulations (http://www.jcpsim.org)
 *
 * Copyright (C) 2002-@year@ Dr. Frank Fischer <frank@jcpsim.org>
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package javax.swing;

import java.awt.Rectangle;
import java.util.HashSet;



class DirtyRegions {
  
  public HashSet<Rectangle> regions;
  
  
  public DirtyRegions(Rectangle r) {
    regions = new HashSet<Rectangle>();
    regions.add(r.getBounds());
  }
  
  public void add(Rectangle r) {
    boolean couldNotUnite = true;
    for (Rectangle rect:regions) {
      if (distance(rect, r) <= DxRepaintManager.maxDistance) {
        union(rect, r);
        couldNotUnite = false;
        break;
      }
    }
    if (couldNotUnite)  regions.add(r.getBounds());
  }
  
  /**
   * Unite two rectangles.
   * @param r1  Rectangle 1, will be expanded so that it contains r2 as well.
   * @param r2  Rectangle 2
   */
  private void union(Rectangle r1, Rectangle r2) {
    int x     = Math.min(r1.x, r2.x);
    int y     = Math.min(r1.y, r2.y);
    r1.width  = Math.max(r1.x+r1.width,  r2.x+r2.width ) - x;
    r1.height = Math.max(r1.y+r1.height, r2.y+r2.height) - y;
    r1.x      = x;
    r1.y      = y;
  }
  
  /**
   * Computes the distance between two rectangles.
   * @param r1  Rectangle 1
   * @param r2  Rectangle 2
   * @return    The distance (negative when the rectangles intersect). 
   */
  private int distance(Rectangle r1, Rectangle r2) {
    return Math.max(Math.max(r2.x-r1.x-r1.width,  
                             r1.x-r2.x-r2.width ),
                    Math.max(r2.y-r1.y-r1.height, 
                             r1.y-r2.y-r2.height));
  }
}