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
    if (args != null && args.length > 0) {
        scenario = args[1];
    } else{
        scenario = "ArterialLine";
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