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
package org.jcpsim.run;

import edu.umd.cs.piccolo.PNode;
import org.jcpsim.scenarios.Block;
import java.util.ArrayList;

public abstract class Scenario extends PNode {
  
  private ArrayList<Block> blocks;
  private String           name;
  
  public Scenario(String name) {
    blocks = new ArrayList<Block>();
  }

  
  public void addBlock(Block block) {
    blocks.add(block);
  }
  
  public ArrayList<Block> getBlocks() {
    return blocks;
  }
  
  public abstract void step(int n);
  
  public String getName() {
    return name;
  }
  
  public void setToDefault() {
    for (Block b:blocks)  b.setToDefault();
  }
}