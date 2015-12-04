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

import org.jcpsim.run.Global;
import org.jcpsim.util.util;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;


public abstract class FrontpanelElement extends PNode {

  private static final Logger logger = Logger.getLogger(FrontpanelElement .class.getName());

  // --------------------------------------------------------------------------

  public double x, y, w, h;
    private final Global global;

  public void setCoord(double x, double y, double w, double h) {

    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    setBounds(0, 0, w, h);
    translate(x, y);
  }

  // --------------------------------------------------------------------------

  private boolean returnMouse = false;

  private  boolean           isPressed;

  public boolean isPressed() { return isPressed; }

  // --------------------------------------------------------------------------

  public boolean selected;
  public boolean activated;

  /**
   * Modifies a given color for the actual <code>FrontpanelElement</code>.
   * If the element is activated or selected the colors are drawn with a
   * different brightness.
   */
  public Color getColor(Color c) {
    if      (activated)  return  util.gamma(c, 1.50);
    else if (selected )  return  util.gamma(c, 0.20);
    else                 return  c;
  }

  public void setCol(Graphics2D g2, Color c) { g2.setColor(getColor(c)); }
  public void setPnt(Graphics2D g2, Color c) { g2.setPaint(getColor(c)); }

  public void drawBackground(Graphics2D g2) { }

  // --------------------------------------------------------------------------

  private Point2D mousePositionBeforeDrag;

  // --------------------------------------------------------------------------
  /**
   * TODO: Should all repaints be removed? 
   *       Should repainting be done at parameter.valueChanged()?
   */
  public FrontpanelElement(double x, double y, double w, double h, Global global) {
    this(global);
    setCoord(x, y, w, h);
  }

  public FrontpanelElement(final Global global) {

    selected  = false;
    activated = false;
    isPressed = false;

    this.global = global;
    
    // -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    addInputEventListener(new PBasicInputEventHandler() {

      public void mousePressed(PInputEvent e) {
        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {  // right press
          help();
          e.setHandled(true);
        } else if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) { // left press
          e.pushCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
          global.activated = true;
          activated = true;
          mousePositionBeforeDrag = (Point2D)e.getCanvasPosition().clone();
          isPressed = true;
          pressed();
          repaint();
        }
      }

      public void mouseDragged(PInputEvent e) {
        PDimension d = e.getCanvasDelta();
        drag((int)(d.getHeight() + d.getWidth()));
      }

      public void mouseReleased(PInputEvent e) {
        super.mouseReleased(e);
        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {   // left release
          e.popCursor();
          activated = false;
          isPressed = false;
          released();
          if (e.getClickCount() > 1)  clicked(e.getClickCount());
          repaint();
//        if (returnMouse)  moveMouse(mousePositionBeforeDrag);
          global.activated = false;
        }
      }

      public void mouseEntered(PInputEvent e) {
        e.pushCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        e.getInputManager().setKeyboardFocus(e.getPath());
        if (!global.activated) {
          entered();
          selected = true;
          repaint();
        }
      }

      public void mouseExited(PInputEvent e) {
        e.popCursor();
        e.getInputManager().setKeyboardFocus(null);
        if (!global.activated) {
          exited();
          selected = false;
          repaint();
        }
      }

      public void mouseWheelRotated(PInputEvent e) {
        step(e.getWheelRotation());
      }

      public void keyPressed(PInputEvent e) {
        switch (e.getKeyCode()) {
        
          case KeyEvent.VK_RIGHT    :  step( +1);   break;
          case KeyEvent.VK_LEFT     :  step( -1);   break;
          case KeyEvent.VK_PAGE_UP  :  step(+10);   break;
          case KeyEvent.VK_PAGE_DOWN:  step(-10);   break;
          
          case KeyEvent.VK_HOME     :  start();     break;
          case KeyEvent.VK_DELETE   :  toDefault(); break;
          case KeyEvent.VK_END      :  end();       break;

          case KeyEvent.VK_F1       :  help();      break;
          case KeyEvent.VK_UP       :  lastFocus(); break;
          case KeyEvent.VK_DOWN     :  nextFocus(); break;
          default:  break;
        }
      }
      
      public void mouseClicked(PInputEvent e) { }
      public void mouseMoved(PInputEvent   e) { }
    });
  }
  // --------------------------------------------------------------------------
  
  public Rectangle2D getScreenBounds(PNode node) {
    Rectangle2D r = global.getCanvas().getCamera().viewToLocal(node.getGlobalBounds());
    Point p1 = new Point((int) r.getX(),               (int) r.getY());
    Point p2 = new Point((int)(r.getX()+r.getWidth()), (int)(r.getY()+r.getHeight()));
    SwingUtilities.convertPointToScreen(p1, (Component)global.getCanvas());
    SwingUtilities.convertPointToScreen(p2, (Component)global.getCanvas());
    return new Rectangle2D.Double(p1.getX(), p1.getY(), p2.getX()-p1.getX(), p2.getY()-p1.getY());
  }
  
  public void moveMouse(PNode node) {
    Rectangle2D r = getScreenBounds(node); 
    try {
      (new Robot()).mouseMove((int)r.getCenterX(), (int)r.getCenterY());
    } catch (java.awt.AWTException e) {
      logger.info("Robots not allowed: " + e);
    }
  }

  // --------------------------------------------------------------------------
  /**
   * Can be overridden.
   * Repaint is done automagically.
   */
  public void entered() { }
  /**
   * Can be overridden.
   * Repaint is done automagically.
   */
  public void exited() { }
  /**
   * Can be overridden.
   */
  public void help() { }
  /**
   * Can be overridden.
   */
  public void lastFocus() { }
  /**
   * Can be overridden.
   */
  public void nextFocus() { }
  /**
   * Can be overridden.
   * Repaint is done automagically.
   */
  public void pressed() { }
  /**
   * Can be overridden.
   * Repaint is done automagically.
   */
  public void released() { }
  /**
   * Can be overridden.
   * Repaint is done automagically.
   */
  public void clicked(int clickCount) {}

  /**
   * One 'atomic' step.
   * Can be overridden ( repaint() should be called for screen update).
   * Defaults to drag(x).
   * One mouse wheel step is one step.
   * Cursor left or right is one step.
   * Cursor up or down are ten steps. 
   */
  public void step(int x) { drag(x); }
  /**
   * Can be overridden ( repaint() should be called for screen update).
   */
  public void start() { }
  /**
   * Can be overridden ( repaint() should be called for screen update).
   */
  public void toDefault() { }
  /**
   * Can be overridden ( repaint() should be called for screen update).
   */
  public void end() { }
  /**
   * Can be overridden ( repaint() should be called for screen update).
   */
  public void drag(int x) { }

  // --------------------------------------------------------------------------

  public void paint(PPaintContext aPaintContext) {
    paintElement(aPaintContext.getGraphics(), aPaintContext.getScale());
  }
  /**
   * To be overridden.
   */
  public void paintElement(Graphics2D g2, double scale) { }
  
  // --------------------------------------------------------------------------
  
  public void macroTimeStep(double t) { }
  
  // --------------------------------------------------------------------------
  
  public void rubberBand(String text, Font font, Graphics2D g2, 
                         double x, double y, double w, double h) {
    
    TextLayout layout = new TextLayout(text, font, g2.getFontRenderContext()); 
    Rectangle2D r2d = layout.getBounds();
    AffineTransform saveAT = g2.getTransform();
    float scaleX = (w < r2d.getWidth()) ?  (float)(w / r2d.getWidth()) : 1.0f;
    float dY     = (float)((h+layout.getAscent()-layout.getDescent())/2.0);  // vertically centered
    g2.transform(new AffineTransform(scaleX, 0, 0, 1, x, y+dY));
    layout.draw(g2, 0, 0);
    g2.setTransform(saveAT);
  }

    public Global getGlobal() {
        return global;
    }
  
}