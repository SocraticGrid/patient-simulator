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
package org.jcpsim.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.logging.Logger;

/**
 */
public class mySliderUI extends BasicSliderUI {

// if (slider.getOrientation() == JSlider.HORIZONTAL) { }

  boolean isLeftToRight(JSlider slider) {
    return true;
  }
  
  protected final int    TICK_BUFFER  = 4;
  
  protected boolean      filledSlider = false;
  
  protected static Color thumbColor;
  
  protected static Color highlightColor;
  
  protected static Color darkShadowColor;
  
  protected static int   trackWidth;
  
  protected static int   tickLength;
  
  protected static Icon  horizThumbIcon;
  
  protected final String SLIDER_FILL  = "JSlider.isFilled";
  
  
  public static ComponentUI createUI(JComponent c) {
    return new mySliderUI();
  }
  

  public mySliderUI() {
    super(null);
  }
  

  public void installUI(JComponent c) {
    trackWidth = ((Integer) UIManager.get("Slider.trackWidth")).intValue();
    tickLength = ((Integer) UIManager.get("Slider.majorTickLength")).intValue();
    horizThumbIcon = UIManager.getIcon("Slider.horizontalThumbIcon");
    
    super.installUI(c);
    
    thumbColor = UIManager.getColor("Slider.thumb");
    highlightColor = UIManager.getColor("Slider.highlight");
    darkShadowColor = UIManager.getColor("Slider.darkShadow");
    
    scrollListener.setScrollByBlock(false);
    
    Object sliderFillProp = c.getClientProperty(SLIDER_FILL);
    if (sliderFillProp != null) {
      filledSlider = ((Boolean) sliderFillProp).booleanValue();
    }
  }
  

  protected PropertyChangeListener createPropertyChangeListener(JSlider slider) {
    return new MetalPropertyListener();
  }
  
  protected class MetalPropertyListener extends
      BasicSliderUI.PropertyChangeHandler {
    
    public void propertyChange(PropertyChangeEvent e) { // listen for slider
                                                        // fill
      super.propertyChange(e);
      
      String name = e.getPropertyName();
      if (name.equals(SLIDER_FILL)) {
        if (e.getNewValue() != null) {
          filledSlider = ((Boolean) e.getNewValue()).booleanValue();
        } else {
          filledSlider = false;
        }
      }
    }
  }
  
  
  public void paintThumb(Graphics g) {
    Rectangle knobBounds = thumbRect;
    g.translate(knobBounds.x, knobBounds.y);
    horizThumbIcon.paintIcon(slider, g, 0, 0);
    g.translate(-knobBounds.x, -knobBounds.y);
  }
  
  

  public void paintTrack(Graphics g) {
    
    g.translate(trackRect.x, trackRect.y);
    
    int trackLeft = 0;
    int trackTop = 0;
    int trackRight = 0;
    int trackBottom = 0;
    
    // Draw the track
    trackBottom = (trackRect.height - 1) - getThumbOverhang();
    trackTop = trackBottom - (getTrackWidth() - 1);
    trackRight = trackRect.width - 1;
    
    if (slider.isEnabled()) {
      g.setColor(MetalLookAndFeel.getControlDarkShadow());
      g.drawRect(trackLeft, trackTop, (trackRight - trackLeft) - 1,
                 (trackBottom - trackTop) - 1);
      
      g.setColor(MetalLookAndFeel.getControlHighlight());
      g.drawLine(trackLeft + 1, trackBottom, trackRight, trackBottom);
      g.drawLine(trackRight, trackTop + 1, trackRight, trackBottom);
      
      g.setColor(MetalLookAndFeel.getControlShadow());
      g.drawLine(trackLeft + 1, trackTop + 1, trackRight - 2, trackTop + 1);
      g.drawLine(trackLeft + 1, trackTop + 1, trackLeft + 1, trackBottom - 2);
    } else {
      g.setColor(MetalLookAndFeel.getControlShadow());
      g.drawRect(trackLeft, trackTop, (trackRight - trackLeft) - 1,
                 (trackBottom - trackTop) - 1);
    }
    
    // Draw the fill
    if (filledSlider) {
      int middleOfThumb = 0;
      int fillTop = 0;
      int fillLeft = 0;
      int fillBottom = 0;
      int fillRight = 0;
      
      middleOfThumb = thumbRect.x + (thumbRect.width / 2);
      middleOfThumb -= trackRect.x; // To compensate for the g.translate()
      fillTop = !slider.isEnabled() ? trackTop : trackTop + 1;
      fillBottom = !slider.isEnabled() ? trackBottom - 1 : trackBottom - 2;
        
      if (!drawInverted()) {
        fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
        fillRight = middleOfThumb;
      } else {
        fillLeft = middleOfThumb;
        fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;
      }
      
      if (slider.isEnabled()) {
        g.setColor(slider.getBackground());
        g.drawLine(fillLeft, fillTop, fillRight, fillTop);
        g.drawLine(fillLeft, fillTop, fillLeft, fillBottom);
        
        g.setColor(MetalLookAndFeel.getControlShadow());
        g.fillRect(fillLeft + 1, fillTop + 1, fillRight - fillLeft, fillBottom
                                                                    - fillTop);
      } else {
        g.setColor(MetalLookAndFeel.getControlShadow());
        g.fillRect(fillLeft, fillTop, fillRight - fillLeft, trackBottom
                                                            - trackTop);
      }
    }
    
    g.translate(-trackRect.x, -trackRect.y);
  }
  

  

  public void paintFocus(Graphics g) {}
  

  protected Dimension getThumbSize() {
    Dimension size = new Dimension();
    size.width = horizThumbIcon.getIconWidth();
    size.height = horizThumbIcon.getIconHeight();
    return size;
  }
  

  /**
   * Gets the height of the tick area for horizontal sliders and the width of
   * the tick area for vertical sliders. BasicSliderUI uses the returned value
   * to determine the tick area rectangle.
   */
  public int getTickLength() {
    return  tickLength + TICK_BUFFER + 1;
  }
  

  /**
   * Returns the shorter dimension of the track.
   */
  protected int getTrackWidth() {
    // This strange calculation is here to keep the
    // track in proportion to the thumb.
    final double kIdealTrackWidth = 7.0;
    final double kIdealThumbHeight = 16.0;
    final double kWidthScalar = kIdealTrackWidth / kIdealThumbHeight;
    
    return (int) (kWidthScalar * thumbRect.height);
  }
  

  /**
   * Returns the longer dimension of the slide bar. (The slide bar is only the
   * part that runs directly under the thumb)
   */
  protected int getTrackLength() {
    return trackRect.width;
  }
  

  /**
   * Returns the amount that the thumb goes past the slide bar.
   */
  protected int getThumbOverhang() {
    return (int) (getThumbSize().getHeight() - getTrackWidth()) / 2;
  }
  

  protected void scrollDueToClickInTrack(int dir) {
    scrollByUnit(dir);
  }
  

  protected void paintMinorTickForHorizSlider(Graphics g, Rectangle tickBounds,
                                              int x) {
    g.setColor(slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel
        .getControlShadow());
    g.drawLine(x, TICK_BUFFER, x, TICK_BUFFER + (tickLength / 2));
  }
  

  protected void paintMajorTickForHorizSlider(Graphics g, Rectangle tickBounds,
                                              int x) {
    g.setColor(slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel
        .getControlShadow());
    g.drawLine(x, TICK_BUFFER, x, TICK_BUFFER + (tickLength - 1));
  }
}