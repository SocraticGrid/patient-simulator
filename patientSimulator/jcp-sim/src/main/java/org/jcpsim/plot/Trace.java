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


import edu.umd.cs.piccolo.PNode;
import org.jcpsim.parameter.Output;
import java.awt.BasicStroke;
import java.awt.Color;


public class Trace {
  
  public final static int DOTS  = 1;
  public final static int LINES = 2;
  
  private String          name;  
  private Output          x;
  private Output          y;
  private int             size;
  private float           width;
  private float           headSize;
  public  Color           color;
  public  Color           headColor;
  private int             mode;
  private PNode           node;
  
  private TraceShape      buffer[];
  private TraceShape      head;
  private int             pointer;
  private BasicStroke     stroke;

  private double          xold;
  private double          yold;
  
  // --------------------------------------------------------------------------
  
  public Trace(String name, Output x, Output y, 
               int size, float width) {
    this(name, x, y, size, width, 4.0f, null, null, LINES, null);
  }
    
  public Trace(String name, Output x, Output y, int size, float width, 
                 float headSize, Color color, Color head, int mode, PNode node) {
      
    this.name      = name;
    this.x         = x;
    this.y         = y;
    this.size      = size;
    this.width     = width;
    this.headSize  = headSize;
    this.color     = color;
    this.headColor = head;
    this.mode      = mode;
    this.node      = node;
    
    buffer         = new TraceShape[size];
    stroke         = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    head           = null;
    pointer        = 0;
  }
  
  // --------------------------------------------------------------------------
  
  public void put(double x, double y) {

    
    if (buffer[pointer] == null)  setOld(x, y);    
    
    pointer = (pointer+1) % size;
    
    if (buffer[pointer] != null) {
      buffer[pointer].set(xold, yold, x, y);
      buffer[pointer].setVisible(true);
//    buffer[(pointer+100)%size].setVisible(false);
      
    } else {
      TraceShape s = null;
      if (mode == DOTS)  s = new TraceDot(x, y, width, color);
                   else  s = new TraceLine(xold, yold, x, y, color, stroke); 
      buffer[pointer] = s;
      node.addChild(s);
    }
    
    if (head == null) {
      head = new TraceDot(x, y, headSize, headColor);
      node.addChild(head);
    } else {
      head.set(xold, yold, x, y);
      head.moveToFront();
    }
    
    setOld(x, y);
  }
  
  // --------------------------------------------------------------------------
  
  public void setOld(double x, double y) {
    xold = x;
    yold = y;
  }
  
  public int             getSize()   { return size; }
  public String          getName()   { return name; }
  public Output          getX()      { return x; }
  public Output          getY()      { return y; }
  public Color           getColor()  { return color; }
  public double          getLastX()  { return xold; }
  
  public void setNode(PNode node) {  this.node = node; }
}