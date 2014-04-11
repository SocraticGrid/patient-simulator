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
