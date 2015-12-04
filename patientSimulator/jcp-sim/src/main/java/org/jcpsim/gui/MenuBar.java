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

import javax.swing.DxRepaintManager;
import javax.swing.JComponent;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.jcpsim.run.Global;


public class MenuBar extends JMenuBar {
  
  static final long serialVersionUID = 0L;
  private static final Logger logger = Logger.getLogger(Global.class.getName());
  
  private PSwing pSwing;
  
  private int maxDistance[] = new int[] {100000, 50, 8, 0, -100000};
  private String dtext[] = new String[] { "all", 
                                          "distance <= 50 pixel",
                                          "distance <= 8 pixel", 
                                          "intersecting", 
                                          "none" };
    private final Global global;

  
  
  public class MyPSwing extends PSwing {
    
    public MyPSwing(JComponent component) {
      super(component);
    }
    
    public boolean setBounds(double x, double y, double width, double height) {
      global.statusline.setOffset(getX() + getWidth() + 10, 3);
      return super.setBounds(x, y, width, height);
    }
  }

  
  // --------------------------------------------------------------------------
  
  private String i18n(String s) { return Global.i18n(s);  }
  
  private JMenu             mSession;  
  private JMenuItem         mSessionDefault;  
  private JMenuItem         mSessionScenario;   
  private JMenuItem         mSessionQuit;
  
  private JMenu             mView; 
  private JMenuItem         mViewRefresh;
  private JCheckBoxMenuItem mViewQuality;
  public  JCheckBoxMenuItem mViewFullscreen;
  private JMenuItem         mViewBackground;
  private JMenuItem         mViewZoom;
  private JMenuItem         mViewNodes;
  
  private JMenu             mSettings;
  private JMenuItem         mSettingsLanguage;
  private JMenuItem         mSettingsDebug;
  
  private JMenu             mHelp;
  private JMenuItem         mHelpUsing;  
  private JMenuItem         mHelpAbout;  

  
  public void setLanguage() {
    
            mSession.setText(i18n("menu.session"));  
     mSessionDefault.setText(i18n("menu.session.setToDefaults"));  
    mSessionScenario.setText(i18n("menu.session.scenario"));   
        mSessionQuit.setText(i18n("menu.session.quit"));
        
               mView.setText(i18n("menu.view")); 
        mViewRefresh.setText(i18n("menu.view.refresh"));
        mViewQuality.setText(i18n("menu.view.highQuality"));
     mViewFullscreen.setText(i18n("menu.view.fullscreen"));
     mViewBackground.setText(i18n("menu.view.backgroundColor"));
           mViewZoom.setText(i18n("menu.view.allBlocks"));
          mViewNodes.setText(i18n("menu.view.oneBlock"));
          
           mSettings.setText(i18n("menu.settings"));
   mSettingsLanguage.setText(i18n("menu.settings.language"));
      mSettingsDebug.setText(i18n("menu.settings.debug"));
      
               mHelp.setText(i18n("menu.help"));
          mHelpUsing.setText(i18n("menu.help.using"));  
          mHelpAbout.setText(i18n("menu.help.about"));  
    
    revalidate();
    repaint();
  }

  // --------------------------------------------------------------------------
  
  public MenuBar(PSwingCanvas canvas,final Global global) {
    super();
    this.global = global;
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // - - - SESSION - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    mSession = new JMenu();  add(mSession);
    
//  JMenuItem mSessionLoad = new JMenuItem("Load");    mFile.add(mSessionLoad);
//  JMenuItem mSessionSave = new JMenuItem("Save");    mFile.add(mSessionSave);
    
    mSessionDefault = new JMenuItem();   mSession.add(mSessionDefault);
    mSessionDefault.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        global.getScenario().setToDefault();
        global.getCanvas().repaint();
        global.status(i18n("menu.session.defaultsHaveBeenSet"));
      }
    });
    
    mSessionScenario = new JMenu();   mSession.add(mSessionScenario);
    for (String s:global.getScenarios()) {
      JMenuItem mi = new JMenuItem(s);  mSessionScenario.add(mi);
      mi.addActionListener(new SetScenarioActionListener(s));
    }
    
    mSessionQuit = new JMenuItem();   mSession.add(mSessionQuit);
    mSessionQuit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        logger.info("regular shutdown");
        System.exit(0);
      }
    });
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // - - - VIEW  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    mView = new JMenu();   add(mView);
    
    
    mViewRefresh = new JMenuItem();   mView.add(mViewRefresh);
    mViewRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        global.getCanvas().repaint();
      }
    });
    
    
    mViewQuality = new JCheckBoxMenuItem();   mView.add(mViewQuality);
    mViewQuality.setState(global.getHighRenderingQuality());
    mViewQuality.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        global.setHighRenderingQuality(!global.getHighRenderingQuality());
        global.getCanvas().repaint();
      }
    });
    
    
    mViewFullscreen = new JCheckBoxMenuItem();   
    if (!global.isApplet()) {
      mView.add(mViewFullscreen);
      //mViewFullscreen.setState(global.getFrame().isFullScreenMode());
      mViewFullscreen.setState(false);
      mViewFullscreen.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) { 
          global.getFrame().setFullScreenMode(!global.getFrame().isFullScreenMode());
        }
      });
    }
    
/*    
    JCheckBoxMenuItem mViewAntialias = new JCheckBoxMenuItem("Antialias");  
    mView.add(mViewAntialias);
*/    
    
    mViewBackground = new JMenuItem();   mView.add(mViewBackground);
    mViewBackground.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        Color c = JColorChooser.showDialog(
                    global.getFrame(), i18n("menu.view.chooseBackgroundColor"),
                    global.getCanvas().getBackground());
        if (c != null)  global.getCanvas().setBackground(c);
      }
    });
    
    
    mView.addSeparator();
    
    
    mViewZoom = new JMenuItem();   mView.add(mViewZoom);
    mViewZoom.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        global.viewNode(null);
      }
    });
    
    
    mViewNodes = new JMenu();   mView.add(mViewNodes);
    for (PNode node:(List<PNode>)global.getScenario().getChildrenReference()) {
      String name = (String)node.getAttribute("name");
      if (name != null) {
        JMenuItem mi = new JMenuItem(name);  mViewNodes.add(mi);
        mi.addActionListener(new SetNodeActionListener(node));
      }
    }

    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // - - - SETTINGS  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    mSettings = new JMenu();   add(mSettings);

    
    mSettingsLanguage = new JMenu();   mSettings.add(mSettingsLanguage);
    for (Locale loc:global.getLanguages()) {
      JMenuItem mi = new JMenuItem(loc.getDisplayLanguage(loc));  mSettingsLanguage.add(mi);
      mi.addActionListener(new SetLanguageActionListener(loc));
    }

    
    
    mSettingsDebug = new JMenu();   mSettings.add(mSettingsDebug);

    
    JMenuItem mSettingsDebugRepaint = new JMenu("unite dirty regions");
    mSettingsDebug.add(mSettingsDebugRepaint);
    
    
    ButtonGroup repaintGroup = new ButtonGroup();
    JRadioButtonMenuItem mSettingsDebugRepaintX[] = new JRadioButtonMenuItem[5];
    for (int i=0; i<5; i++) {
      mSettingsDebugRepaintX[i] = new JRadioButtonMenuItem(dtext[i]);  
      mSettingsDebugRepaint.add(mSettingsDebugRepaintX[i]);
      mSettingsDebugRepaintX[i].addActionListener(new RepaintActionListener(i));
      repaintGroup.add(mSettingsDebugRepaintX[i]);
    }
    mSettingsDebugRepaintX[3].setSelected(true);
    DxRepaintManager.currentManager(null).setMaxDistance(maxDistance[3]);
    
    
    JCheckBoxMenuItem mSettingsRegion = new JCheckBoxMenuItem("show region management"); 
    mSettingsDebug.add(mSettingsRegion);
    mSettingsRegion.setState(PDebug.debugRegionManagement);
    mSettingsRegion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
        global.getCanvas().repaint();
      }
    });
    

    JCheckBoxMenuItem mSettingsBounds = new JCheckBoxMenuItem("show full bounds");
    mSettingsDebug.add(mSettingsBounds);
    mSettingsBounds.setState(PDebug.debugFullBounds);
    mSettingsBounds.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        PDebug.debugFullBounds = !PDebug.debugFullBounds;
        global.getCanvas().repaint();
      }
    });
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // - - - HELP  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    mHelp = new JMenu();   add(mHelp);
    
    
    mHelpUsing = new JMenuItem();   mHelp.add(mHelpUsing);
    mHelpUsing.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        global.help("using");
      }
    });
    
    
    mHelp.addSeparator();
    
    
    mHelpAbout = new JMenuItem();   mHelp.add(mHelpAbout);
    mHelpAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) { 
        JOptionPane.showMessageDialog(global.getFrame(), 
          "jCpSim - Java Cardiopulmonary Simulations\n" +
          "           " + i18n("menu.help.about.version") + " " + global.getVersion() + "\n" +
          "            " + i18n("menu.help.about.by") + " Dr. Frank Fischer\n" +
          "           http://www.jcpsim.org");
      }
    });
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    
    setLanguage();
    pSwing = new MyPSwing(this);
    canvas.getCamera().addChild(pSwing);  // sticky
    pSwing.moveToFront();
  }
  
  // --------------------------------------------------------------------------

  
  public class RepaintActionListener implements ActionListener {
    int d;
    
    public RepaintActionListener(int i) {
      d = maxDistance[i];
    }
    
    public void actionPerformed(ActionEvent ae) { 
      DxRepaintManager.currentManager(null).setMaxDistance(d);
    }
  }

  
  public class SetNodeActionListener implements ActionListener {
    
    PNode  node;
    
    public SetNodeActionListener(PNode  node) {
      super();
      this.node = node;
    }
    
    public void actionPerformed(ActionEvent ae) {
      global.viewNode(node);
    }
  }
  
  
  public class SetScenarioActionListener implements ActionListener {
    
    String scenarioName;
    
    public SetScenarioActionListener(String scenarioName) {
      super();
      this.scenarioName = scenarioName;
    }
    
    public void actionPerformed(ActionEvent ae) {
      global.setScenario(scenarioName);
    }
  }

  
  public class SetLanguageActionListener implements ActionListener {
    
    Locale loc;
    
    public SetLanguageActionListener(Locale loc) {
      super();
      this.loc = loc;
    }
    
    public void actionPerformed(ActionEvent ae) {
      global.setLocale(loc);
      global.refreshAll();
    }
  }
}