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
package org.jcpsim.plot;

import edu.umd.cs.piccolo.PNode;

public abstract class TraceShape extends PNode {

  /*
   * Set coordinates.
   * @param x1  x coordinate of last position
   * @param y1  x coordinate of last position
   * @param x2  x coordinate of actual position
   * @param y2  x coordinate of actual position
   */
  public abstract void set(double x1, double y1, double x2, double y2);
}
