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
package org.jcpsim.plot;


import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolo.util.PPaintContext;

import org.jcpsim.gui.FText;
import org.jcpsim.run.Global;
import org.jcpsim.util.util;

/*
 * @todo  pluggable look and feel (LAF)
 * todo: combine scaleX and calcX
 */
public class PlotNode extends PNode {
  
  static final long serialVersionUID = 0L;
  
  private String   title;
  public  boolean  cyclic;
  private Trace[]  traces;
  
  public String  getTitle()              { return title;  }
  public void    setCyclic(boolean flag) { cyclic = flag; }
  public Trace[] getTraces()             { return traces; }
  public void    setDots(boolean flag)   { dots   = flag; }
  
  //-----------------------------------------------------------------------------------
  
  public  PClip   clip;
  public  boolean dots;

  public  double  minX;
  public  double  minY;
  public  double  maxX;
  public  double  maxY;
  
  
  public AffineTransform doTransform(double sX0, double dX0, double sX1, double dX1,
                                     double sY0, double dY0, double sY1, double dY1) {
    double m00 = (dX1-dX0) / (sX1-sX0);
    double m11 = (dY1-dY0) / (sY1-sY0);
    double m02 = dX0 - m00 * sX0;
    double m12 = dY0 - m11 * sY0;
    return new AffineTransform(m00, 0, 0, m11, m02, m12);
  }
  
  //-----------------------------------------------------------------------------------
  
  Font   font1      = new Font("Lucida Sans Regular", Font.BOLD,  14);
  Font   font2      = new Font("Lucida Sans Regular", Font.PLAIN, 12);
  Font   font2b     = new Font("Lucida Sans Regular", Font.BOLD,  12);
  Font   font3      = new Font("Lucida Sans Regular", Font.PLAIN,  9);
  Stroke lineStroke = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  Color  lineColor  = new Color(180, 180, 180);
  Color  lineColor0 = new Color(  0,   0,   0);
  Color  clipColor  = new Color(230, 230, 255);
  Color  nodeColor  = new Color(220, 220, 220);
    
  //-----------------------------------------------------------------------------------
  
  private static Color colors[] = new Color[] {
    new Color(  0,  0,255),    
    Color.BLACK,
    new Color(220,220,  0),
    new Color(  0,220,  0)
  };
  
  private static Color headColor =  new Color(220,  0,  0);
  
  private Color getColor(int i) {
    return colors[i % colors.length];
  }
  
  
  FText titleText;
  FText xAxisText;
  FText yAxisText;
  FText xNumText[];
  FText yNumText[];
  FText legendText[];
  
  public PlotNode(String title, boolean cyclic, Trace trace) {
    this(title, cyclic, new Trace[] { trace }); 
  }
  
  public PlotNode(String title, boolean cyclic, Trace trace, Double maxX) {
    this(title, cyclic, new Trace[] { trace }, maxX); 
  }
  
  
  public PlotNode(String title, boolean cyclic, Trace[] traces) {
      this(title, cyclic, traces, null);
  }
  
  public PlotNode(String title, boolean cyclic, Trace[] traces, Double maxX) {
    
    this.title  = title;
    this.traces = traces;
    
    addAttribute("name", title);
    
    clip = new PClip();
    clip.setStrokePaint(null);
    addChild(clip);    
    
    setCyclic(cyclic);
    setDots(false);
    Trace t = traces[0];
    
    titleText = new FText(title, font1, Color.BLACK, 0.5, false, this);                                        
    xAxisText = new FText(t.getX().getTypeWithUnit(),font2, Color.BLACK, 0.5, false, this); 
    yAxisText = new FText(t.getY().getTypeWithUnit(),font2, Color.BLACK, 0.5, true,  this); 

    legendText = new FText[traces.length];
    int n=0;
    for (Trace trace:traces) {
      if (trace.color     == null)  trace.color     = getColor(n);
      if (trace.headColor == null)  trace.headColor = headColor;
      legendText[n++] = new FText(trace.getName(), font2b, 
                                  util.withAlpha(trace.getColor(), 255), 
                                  1.0, false, clip);
      trace.setNode(clip); 
    }
    
    // set min and max
    this.minX = Double.POSITIVE_INFINITY;
    this.minY = Double.POSITIVE_INFINITY;
    this.maxX = Double.NEGATIVE_INFINITY;
    this.maxY = Double.NEGATIVE_INFINITY;
    for (Trace trace:traces) {
      this.minX = Math.min(this.minX, trace.getX().getMin());
      this.minY = Math.min(this.minY, trace.getY().getMin());
      this.maxX = Math.max(this.maxX, trace.getX().getMax());
      this.maxY = Math.max(this.maxY, trace.getY().getMax());
    }
    
    //fixed maxX value? -> use it
    if (maxX != null){
        this.maxX = maxX;
    }
    
  }
  
  //-----------------------------------------------------------------------------------
  
  public double calcX(double x) { 
      return paW / (maxX - minX) * (x  - minX) +  paX; 
  }
  
  public double calcY(double y) { 
      return paH / (minY - maxY) * (y  - maxY) +  paY; 
  }

  public AffineTransform at;
  public ComputeTics     scaleX;
  public ComputeTics     scaleY;

  // plot area dimensions
  double paX;
  double paY;
  double paW;
  double paH;
  
  
  public boolean setBounds(double x, double y, double width, double height) {
    if (super.setBounds(x, y, width, height)) {
      
      int lMargin = 42;
      int rMargin = 13;
      int tMargin = 16;
      int bMargin = 25;
      
      paX = x+lMargin;
      paY = y+tMargin;
      paW = width-lMargin-rMargin;
      paH = height-tMargin-bMargin;
      
      clip.setPathToRectangle((float)paX, (float)paY, (float)paW, (float)paH);
      
      at     = doTransform(minX, paX, maxX, paX+paW, maxY, paY, minY, paY+paH);
      scaleX = new ComputeTics(minX, maxX, paW / 40); // tics approx. every 40 pixels
      scaleY = new ComputeTics(minY, maxY, paH / 40); // tics approx. every 40 pixels
      
      titleText.setBounds(paX, y,                    paW,     tMargin-2);
      xAxisText.setBounds(paX, y+height-bMargin/2-2, paW,     bMargin/2);
      yAxisText.setBounds(x,   paY,                  tMargin, paH);

      // - - - x numbers and vertical lines - - - - - - - - - - - - - - - - - - - - - - - - 
      xNumText = new FText[scaleX.tics+1];
      int n=0;
      for (double i=scaleX.ticmin; i<=maxX; i+=scaleX.ticd) { 
        xNumText[n] = new FText(scaleX.format(i), font3, Color.BLACK, 0.5, false, this);
        xNumText[n].setBounds(calcX(scaleX.ticmin + (n-0.5)*scaleX.ticd), 
                              y+height-bMargin+2, 
                              paW / (maxX - minX) * scaleX.ticd, 
                              bMargin/2-4);
        n++;
      }
      // - - - y numbers and horizontal lines - - - - - - - - - - - - - - - - - - - - - - - 
      yNumText = new FText[scaleY.tics+1];
      n=0;
      for (double i=scaleY.ticmin; i<=maxY; i+=scaleY.ticd) {
        yNumText[n] = new FText(scaleY.format(i), font3, Color.BLACK, 1.0, false, this);
        yNumText[n].setBounds(x+tMargin,
                              calcY(scaleY.ticmin + (n+0.5)*scaleY.ticd), 
                              lMargin-tMargin-3, 
                              paH / (maxY - minY) * scaleY.ticd);
        n++;
      }
      
      // - - - legend - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
      for (int i=0; i<traces.length; i++) {
        legendText[i].setBounds(x+lMargin, y+tMargin+2+16*i, width-lMargin-rMargin-4, 16); 
      }
      return true;
    }
    return false;
  }
  
  //-----------------------------------------------------------------------------------
  
  public void update() {
    for (Trace trace:traces) {
      double y = calcY(trace.getY().get());
      if (cyclic) {
        double x = calcX(trace.getX().get() % maxX);
        if  (x > trace.getLastX()){
            trace.put(x, y);
        } else{
            trace.setOld(x,y);
        }
      } else {
        double x = calcX(trace.getX().get());
        trace.put(x, y);
      }
    }
  }
  
  //-----------------------------------------------------------------------------------
  
  static protected Line2D gridLine = new Line2D.Double();

  
  public void paint(PPaintContext paintContext) {
    Graphics2D  g2    = paintContext.getGraphics();
    Rectangle2D lclip = paintContext.getLocalClip();
    Global.setRenderingQuality(g2);
    
    g2.setPaint(nodeColor);    // paint bg of frame
    g2.fill(getBounds());
    
    g2.setPaint(clipColor);    // paint bg of plotting area
    g2.fill(clip.getBounds());
    
    g2.setStroke(lineStroke);  
    int n=0;                   // paint vertical coordinate lines
    for (double i=scaleX.ticmin; i<=maxX; i+=scaleX.ticd) { 
      g2.setColor(scaleX.isZero(i) ? lineColor0 : lineColor);
      double x = calcX(scaleX.ticmin + n*scaleX.ticd);
      gridLine.setLine(x, clip.getY(), x, clip.getY()+clip.getHeight());
      if (lclip.intersectsLine(gridLine))  g2.draw(gridLine);
      n++;
    }
    n=0;                       // paint horizontal coordinate lines
    for (double i=scaleY.ticmin; i<=maxY; i+=scaleY.ticd) { 
      g2.setColor(scaleY.isZero(i) ? lineColor0 : lineColor);
      double y = calcY(scaleY.ticmin + n*scaleY.ticd);
      gridLine.setLine(clip.getX(), y, clip.getX()+clip.getWidth(), y);
      if (lclip.intersectsLine(gridLine))  g2.draw(gridLine);
      n++;
    }
  }
}