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