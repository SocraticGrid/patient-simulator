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
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
import java.util.logging.Logger;


public class Clock extends PNode {
  
  static final long serialVersionUID = 0L;
  
  private Ellipse2D ellipse;
  private double    frame;
  private double    cycle;
  
  
  public Clock() {
    getEllipse();
  }
  

  public Ellipse2D getEllipse() {
    if (ellipse == null) ellipse = new Ellipse2D.Double();
    return ellipse;
  }
  

  public boolean setBounds(double x, double y, double width, double height) {
    if (super.setBounds(x, y, width, height)) {
      ellipse.setFrame(x, y, width, height);
      return true;
    }
    return false;
  }
  

  public boolean intersects(Rectangle2D aBounds) {
    return getEllipse().intersects(aBounds);
  }
  

  public void paint(PPaintContext aPaintContext) {
    
    Graphics2D g2 = aPaintContext.getGraphics();
    g2.setPaint(new Color(200, 200, 230));
    g2.fill(ellipse);
    
    double x = ellipse.getCenterX() + ellipse.getWidth()  * 0.5 * Math.cos(cycle);
    double y = ellipse.getCenterY() + ellipse.getHeight() * 0.5 * Math.sin(cycle);
    Line2D line = new Line2D.Double(ellipse.getCenterX(), ellipse.getCenterY(), x, y);
    g2.setColor(new Color(255, 100, 100));
    g2.draw(line);
    
    x = ellipse.getCenterX() + ellipse.getWidth() * 0.4 * Math.cos(frame);
    y = ellipse.getCenterY() + ellipse.getHeight() * 0.4 * Math.sin(frame);
    Ellipse2D dot = new Ellipse2D.Double(x - 2, y - 2, 4, 4);
    g2.setPaint(new Color(100, 100, 255));
    g2.fill(dot);
  }
  

  public void setTime(int step, int steps, double timeInCycle, double cycleTime) {
    frame = 2.0 * Math.PI * step / steps;
    cycle = 2.0 * Math.PI * timeInCycle / cycleTime;
    repaint();
  }
  /*
   * public void flashNode(final PNode aNode) { PActivity flash = new
   * PActivity(5000) { boolean fRed = true; protected void step(long time) {
   * super.step(time); if (fRed) { aNode.setPaint(Color.red); } else {
   * aNode.setPaint(Color.green); } fRed = !fRed; } }; // Must schedule the
   * activity with the root for it to run. getRoot().addActivity(flash); }
   */
}
