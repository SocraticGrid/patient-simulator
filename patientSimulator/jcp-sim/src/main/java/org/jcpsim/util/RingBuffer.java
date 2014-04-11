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
package org.jcpsim.util;


public class RingBuffer<Item> {

  private Item[] buffer;   // queue elements
  private int    length;   // maximum number of elements
  private int    n;        // number of elements on queue
  private int    newest;   // index of newest element
  

  public RingBuffer(int length) {
    reset(length);
  }

  @SuppressWarnings("unchecked")   // cast needed since (no generic array creation)
  public void reset(int length) {
    buffer      = (Item[])new Object[length];
    this.length = length;
    n           = 0;
    newest      = 0;
  }
  
  public boolean isEmpty()   { return n == 0; }
  public int     size()      { return n;      }
  public int     getLength() { return length; }

  /**
   * Writes an item value to the buffer.
   * @param item  the value
   */
  public void put(Item item) {
    if (++newest == length)  newest = 0;
    buffer[newest] = item;
    if (n < length) n++;
  }
  
  /**
   * Gets the actual element.
   * Does not check if the buffer is empty (for sake of speed).
   * @return the newest (= actual) element
   */
  public Item get() {
    return  buffer[newest];
  }

  /**
   * Retrieves a value from the buffer.
   * To read all values:
   * <pre>for (int i=0; i<size(); i++)  x = get(i);</pre>.
   * @param i 0: the newest value in the buffer;
   *          1: the newest but one value;
   *          size()-1: the oldest value
   * @return the value
   */
  public Item get(int i) {
    return  buffer[(newest + length - i) % length];
  }
}