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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.awt.Component;
import java.awt.Rectangle;



class DirtyComponents {
  
  public  Map<Component,DirtyRegions> components;
  public  List<Component>             roots;

  
  public DirtyComponents() {
    components = new IdentityHashMap<Component,DirtyRegions>();
  }
  
  public void clear() {
    components.clear();
  }
  
  public int size() {
    return components.size();
  }
  
  public void remove(Component c) {
    components.remove(c);
  }
  
  /**
   * Extends the dirty region for the specified component to include
   * the new region.
   * @return false if <code>c</code> is not yet marked dirty.
   */
  public synchronized boolean extend(Component c, Rectangle r) {
    DirtyRegions region = components.get(c);
    if (region == null)  return false;
    region.add(r);
    return true;
  }

  public void put(Component c, Rectangle r) {
    components.put(c, new DirtyRegions(r));
  }

  private boolean computeIntersection(Rectangle r1, Rectangle r2) {
    SwingUtilities.computeIntersection(0, 0, r1.width, r1.height, r2);
    return  r2.isEmpty();
  }
  
  /*
   * Find the highest parents that are dirty.
   */
  int collect() {
    if (components.size() == 0)  return 0;
    roots = new ArrayList<Component>(components.size());
    for (Component dirtyComponent:components.keySet()) collect(dirtyComponent);
    return roots.size();
  }
  
  
  void collect(Component dirtyComponent) {
    // When we get out of this rootDx and rootDy will contain the translation 
    // from the rootDirtyComponent's coordinate system to the coordinates of 
    // the original dirty component.
    // The tmp Rect is also used to compute the visible portion of the dirtyRect.

    Component component          = dirtyComponent;
    Component rootDirtyComponent = dirtyComponent;
    Rectangle tmp                = new Rectangle();
    Rectangle bounds             = dirtyComponent.getBounds();
  
    int dx     = 0;
    int dy     = 0;
    int rootDx = 0;
    int rootDy = 0;
  
    for (Rectangle region:components.get(dirtyComponent).regions) {
      tmp.setBounds(region);

//    System.out.println("dirty component: " + pRect(dirtyComponent.getBounds()) + " region: " + pRect(tmp)); 
      if (computeIntersection(bounds, tmp))  return;

      for (;;) {
        if (!(component instanceof JComponent))  break;
        if (component.getParent() == null)  break;
        component = component.getParent();

        dx += bounds.x;
        dy += bounds.y;
        tmp.setLocation(tmp.x + bounds.x, tmp.y + bounds.y);

        bounds = component.getBounds();
        if (computeIntersection(bounds, tmp))  return;

        if (components.get(component) != null) {
          rootDirtyComponent = component;
          rootDx = dx;
          rootDy = dy;
        }
      } 
      if (dirtyComponent != rootDirtyComponent) {
        tmp.setLocation(tmp.x + rootDx - dx, tmp.y + rootDy - dy);
        DirtyRegions reg = components.get(rootDirtyComponent);
        for (Rectangle r:reg.regions) {
//        System.out.println("    uniting " + pRect(tmp)  + " and " + pRect(r));
          SwingUtilities.computeUnion(tmp.x,tmp.y,tmp.width,tmp.height,r);
        }
      }
      // If we haven't seen this root before, then we need to add it to the
      // list of root dirty Views.
      if (!roots.contains(rootDirtyComponent))  roots.add(rootDirtyComponent);
    }
  }
  
  
  String pRect(Rectangle r) {  // print rectangle  
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    sb.append((int)r.getX());
    sb.append(",");
    sb.append((int)r.getY());
    sb.append(" ");
    sb.append((int)r.getWidth());
    sb.append("x");
    sb.append((int)r.getHeight());
    sb.append("]");
    return sb.toString();
  }
}
