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
package org.jcpsim.piccolo;


import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import java.awt.event.InputEvent;


/**
 * see:  edu.umd.cs.piccolox.event.PZoomToEventHandler<br>
 * <b>PZoomToEventHandler</b> is used to zoom the camera view to the node
 * clicked on with button one.
 * <P>
 * @version 1.0
 * @author Frank Fischer (adapted from Jesse Grosjean)
 */
public class PZoomToEventHandler extends PBasicInputEventHandler {

  static final long serialVersionUID = 0L;

  boolean majorNodes;


  public PZoomToEventHandler(boolean majorNodes) {
    this.majorNodes = majorNodes;
    setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
  }


  public void mousePressed(PInputEvent aEvent) {
    zoomTo(aEvent);
  }


  protected void zoomTo(final PInputEvent aEvent) {
    PNode picked = aEvent.getPickedNode();

    if (picked instanceof PCamera) {
      putil.zoomTo((PCamera)picked, null, 0.05);
    } else {
      if (majorNodes) {
        while (!(picked.getParent() instanceof PLayer)) {
          picked = picked.getParent();
        }
      }
      putil.zoomTo(aEvent.getCamera(), picked, 0.05);
    }
  }
}
