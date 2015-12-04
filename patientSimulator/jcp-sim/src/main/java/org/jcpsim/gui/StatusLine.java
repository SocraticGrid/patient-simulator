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
package org.jcpsim.gui;

import java.awt.Color;

import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class StatusLine extends PText {
  
  public StatusLine(PSwingCanvas canvas) {
    setPaint(new Color(249,179,179));
    canvas.getCamera().addChild(this);  // sticky
  }
  
  public void status(String text) {
    StringBuffer sb = new StringBuffer();
    if (text.length() > 0) {
      sb.append(">>> ");
      sb.append(text);
      sb.append(" <<<");
    }
    String s = sb.toString();
    if (s.equals(getText()))  return;
    setText(s);
  }
}
