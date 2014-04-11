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
package org.jcpsim.examples;

import javax.swing.DxRepaintManager;
import javax.swing.RepaintManager;



import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;

public class TestRegionManagement extends PFrame {
  
  /*
   * Works like PText but waits some ms before painting.
   */
  public class PDelay extends PText {
    int ms;
    
    public PDelay(int ms, int dx, int dy, PFrame frame) {
      super();
      this.ms = ms;
      translate(dx, dy);
      frame.getCanvas().getLayer().addChild(this);
    }
    
    public void paint(PPaintContext paintContext) {
      long   until = System.currentTimeMillis() + ms;
      while (until > System.currentTimeMillis());
      super.paint(paintContext);
    }
  }

  PDelay quick1a, quick1b, quick2, slow;
  
  public void initialize() {
    setName("test Frame");
    getCanvas().setName("test Canvas");
    quick1a = new PDelay(  1,100,  0,this); //   1 ms, upper right
    quick1b = new PDelay(  1,140,  0,this); //   1 ms, near quick1a
    quick2  = new PDelay(  1,  0,100,this); //   1 ms, lower left
    slow    = new PDelay(200, 50, 50,this); // 200 ms, between the quick nodes
    
    slow.addActivity(new PActivity(-1, 10) {
      int n = 0;
      protected void activityStep(long elapsedTime) {
        super.activityStep(elapsedTime);
        quick1a.setText(Integer.toString(n));
        quick1b.setText(Integer.toString(n));
        quick2.setText(Integer.toString(n));
        if (n % 73 == 0) slow.setText(Integer.toString(n));
        n++;
      }
    });
  }

  public static void main(String[] args) { 
    RepaintManager.setCurrentManager(new DxRepaintManager());
    DxRepaintManager.currentManager(null).setMaxDistance(20);
    PDebug.debugRegionManagement = true;
    PDebug.debugPaintCalls       = true;
    new TestRegionManagement();  
  }
}