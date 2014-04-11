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

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PUtil;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.awt.Toolkit;
import javax.swing.JTabbedPane;
import org.jcpsim.clock.Clock;
import org.jcpsim.clock.RealTimeClock;
import org.jcpsim.clock.SimulationClock;
import org.jcpsim.gui.TopMenu;


/*
 * Runs jCpSim as an Application.
 * Starts up global.init
 */
public class myApplication extends PFrame {
  
  private static final Clock mainClock = new SimulationClock();
  private static final Clock auxClock = new RealTimeClock();  
    
  static final long serialVersionUID = 0L;
  
  static{
      PUtil.clock = mainClock;
  }
  
  String args[];
  
  public myApplication(String args[], String title) {
    this(new PSwingCanvas(), title);
    this.args = args;
  }
  

  public myApplication(PCanvas aCanvas, String title) {
    super(title, false, aCanvas);
  }
  

  public void initialize() {
    String scenario = null;
    if (args != null) {
      if (args.length > 0)  scenario = args[1];
    }

    this.setBounds(10,10, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height );
    
    TopMenu topMenu = new TopMenu();
    topMenu.setBounds(0, 0, this.getWidth(), 30);
    getCanvas().add(topMenu);
    
    JTabbedPane tabPane = new JTabbedPane();
    tabPane.setBounds(0, topMenu.getY()+topMenu.getHeight(), this.getWidth(), this.getHeight()-(topMenu.getY()+topMenu.getHeight()));
    
    
    PUtil.clock = mainClock;
    PSwingCanvas ps1 = new PSwingCanvas();
    ps1.setBounds(this.getBounds());
    ps1.setBackground(Global.MODE.SIM.getBlockColor().brighter());
            
//    PUtil.clock = auxClock;
//    PSwingCanvas ps2 = new PSwingCanvas();
//    ps2.setBounds(this.getBounds());
//    ps2.setBackground(Global.MODE.AUX.getBlockColor().brighter());
    
    tabPane.add("Main", ps1);
//    tabPane.add("AUX", ps2);
    getCanvas().add(tabPane);

    PUtil.clock = mainClock;
    new Global(ps1, null, this, scenario, topMenu, Global.MODE.SIM);
//    PUtil.clock = auxClock;
//    new Global(ps2, null, this, scenario, topMenu, Global.MODE.AUX);
  }
  

  public static void main(String[] args) {
    //PUtil.clock = new RealTimeClock();
    
    
    String title = "jCpSim";
    if (args != null) {
      if (args.length > 0){
          title += " - "+args[1];
      }
    }
    
    new myApplication(args, title);
  }
}