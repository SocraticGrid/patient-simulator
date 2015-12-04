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