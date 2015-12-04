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