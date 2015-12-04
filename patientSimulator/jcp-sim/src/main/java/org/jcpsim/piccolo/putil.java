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

/*
 * jCpSim - Java CardioPulmonary SIMulations (http://www.jcpsim.org) Copyright
 * (C) 2002-2005 Dr. Frank Fischer <frank@jcpsim.org> This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;


/**
 * Utilities for piccolo
 * @author Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 */
public final class putil {
  static final long           serialVersionUID = 0L;
  private static final Logger logger           = Logger.getLogger(putil.class
                                                   .getName());
  
  
  private putil() {}
  

  /**
   * @param camera the camera which shall zoom
   * @param node the node which shall be zoomed to full canvas size if
   *          node==null all nodes are zoomed.
   * @param zoom 0.0: completely fills the screen, 0.1 add 10% margin on each
   *          side.
   */
  public static void zoomTo(final PCamera camera, PNode node, double zoom) {
    PBounds pb = (node == null) ? camera.getUnionOfLayerFullBounds()
                                : node.getGlobalFullBounds();
    pb.inset(-zoom * pb.width, -zoom * pb.height);
    camera.animateViewToCenterBounds(pb, true, 500);
  }
  

  // --------------------------------------------------------------------------
  public static void removeAllListeners(PNode node) {
    EventListenerList ell = node.getListenerList();
    Object[] listeners = ell.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      int n = ell.getListenerCount();
      ell.remove(PInputEventListener.class,
                 (PInputEventListener) listeners[i + 1]);
      if (n == ell.getListenerCount()) logger.severe("Could not remove "
                                                     + ((Class) listeners[i])
                                                         .getName());
    }
  }
}
