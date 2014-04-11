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

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JApplet;

import org.jcpsim.gui.MenuBar;
import org.jcpsim.gui.StatusLine;
import org.jcpsim.piccolo.putil;
import org.jcpsim.scenarios.SimpleRespirator;
import org.jcpsim.scenarios.ArterialLine;
import org.jcpsim.scenarios.PkPd;
import org.jcpsim.scenarios.Block;
import org.jcpsim.util.Utf8ResourceBundle;


import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PUtil;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.awt.Color;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.jcpsim.gui.InputElement;
import org.jcpsim.gui.OutputElement;
import org.jcpsim.gui.TopMenu;
import org.jcpsim.jmx.JCpSimClockMgmt;
import org.jcpsim.jmx.JCpSimClockMgmtMBean;
import org.jcpsim.jmx.JCpSimMgmt;
import org.jcpsim.jmx.JCpSimMgmtMBean;
import org.jcpsim.scenarios.CustomRespirator;

/**
 *
 * @author Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$ TODO: write JavaDoc. TODO: web documentation. TODO: update
 * website, upload software.
 */
public final class Global {

    public  enum MODE{
        SIM(
                "service:jmx:rmi:///jndi/rmi://localhost:9999/JCpSim",
                9999,
                new Color(220, 220, 220), 
                new Color(108, 147, 224), 
                new Color(141, 202, 245), 
                new Color(200, 200, 0),
                new Color(255, 255, 100),
                1000
        ),
        AUX(
                "service:jmx:rmi:///jndi/rmi://localhost:9998/JCpSim",
                9998,
                new Color(220, 200, 200), 
                new Color(108, 147, 224), 
                new Color(141, 202, 245), 
                new Color(200, 200, 0),
                new Color(255, 255, 100),
                
                //new Color(220,0,0), 
                //Color.CYAN, 
                //Color.MAGENTA, 
                //Color.CYAN, 
                //Color.MAGENTA,
                1000
        );

        
        private final String JMXUrl;
        private final int JMXPort;
        private final Color blockColor;
        private final Color inputFgSliderColor;
        private final Color inputBgSliderColor;
        private final Color outputFgSliderColor;
        private final Color outputBgSliderColor;
        private int bloodGasCalculationThershold;

        private MODE(String JMXUrl, int JMXPort, Color blockColor, Color inputFgSliderColor, Color inputBgSliderColor, Color outpuFgSliderColor, Color outpuBgSliderColor, int bloodGasCalculationThershold) {
            this.JMXUrl = JMXUrl; 
            this.JMXPort = JMXPort; 
            this.blockColor = blockColor;
            this.inputFgSliderColor = inputFgSliderColor;
            this.inputBgSliderColor = inputBgSliderColor;
            
            this.outputFgSliderColor = outpuFgSliderColor;
            this.outputBgSliderColor = outpuBgSliderColor;
            
            this.bloodGasCalculationThershold = bloodGasCalculationThershold;
        }

        public String getJMXUrl() {
            return JMXUrl;
        }

        public int getJMXPort() {
            return JMXPort;
        }

        public Color getBlockColor() {
            return blockColor;
        }

        public Color getInputFgSliderColor() {
            return inputFgSliderColor;
        }

        public Color getInputBgSliderColor() {
            return inputBgSliderColor;
        }

        public Color getOutputFgSliderColor() {
            return outputFgSliderColor;
        }

        public Color getOutputBgSliderColor() {
            return outputBgSliderColor;
        }

        public int getBloodGasCalculationThershold() {
            return bloodGasCalculationThershold;
        }
        
        
    }
    
    final static long serialVersionUID = 0L;
    private static final Logger logger = Logger.getLogger(Global.class.getName());
    public  String basedirpath;
    private  PSwingCanvas canvas = null;
    private  JApplet applet = null;
    private  PFrame frame = null;
    private  boolean isAnApplet;
    private  String version;
    private  MenuBar menubar;
    public  StatusLine statusline;
    private  MODE mode;
    private TopMenu topMenu;
    

    public  PSwingCanvas getCanvas() {
        return canvas;
    }

    public  JApplet getApplet() {
        return applet;
    }

    public  PFrame getFrame() {
        return frame;
    }

    public  boolean isApplet() {
        return isAnApplet;
    }

    public  String getVersion() {
        return version;
    }
    // --------------------------------------------------------------------------
    private PActivity pActivity;

    protected Global() {
        
    }

    /**
     * The starting point.
     */
    protected Global(PSwingCanvas c, JApplet applet, PFrame frame, String scenarioName, TopMenu topMenu, MODE mode) {
        this();

        if (mode == null){
            mode = MODE.SIM;
        }
        this.topMenu = topMenu;
        
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new MyFormatter());
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
        logger.info("jCpSim started");
        logger.log(Level.INFO, "java   .version    = {0}", System.getProperty("java.version"));
        logger.log(Level.INFO, "java.vm.version = {0}", System.getProperty("java.vm.version"));

        canvas = c;
        this.applet = applet;
        this.frame = frame;
        this.mode = mode;
        
        isAnApplet = (applet != null);
        setLocale(Locale.getDefault());

        // - - - load version - - - - - - - - - - - - - - - - - - - - - - - - - 
        version = "could not load version.properties";
        try {
            Properties versionProp = new Properties();
            InputStream istream = Global.class.getResourceAsStream("version.properties");
            if (istream != null) {
                versionProp.load(istream);
                version = versionProp.getProperty("version") + "  ("
                        + versionProp.getProperty("date") + ")";
            }
        } catch (IOException e) {
            logger.info("could not load version.properties");
        }
        logger.log(Level.INFO, "version:  {0}", getVersion());

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

        logger.log(Level.INFO, "it runs as an: {0}", (isAnApplet ? "applet" : "application"));

        canvas.removeInputEventListener(canvas.getZoomEventHandler());
        canvas.removeInputEventListener(canvas.getPanEventHandler());
        setHighRenderingQuality(false);

        setScenario(scenarioName);
        statusline = new StatusLine(canvas);
        menubar = new MenuBar(canvas, this);

        refreshAll();

        final int fView = 20; // frames per second
        int fSampling = 100; // sampling frequency

        
        pActivity = new PActivity(-1, 1000 / fView, 0) {
            int n = 0;
            long oldTime = 0;

            protected void activityStep(long elapsedTime) {
                super.activityStep(elapsedTime);
                n++;
                for (int i = 0; i < 5; i++) {
                    scenario.step(n);
                }

                if (getFrame() != null) {
                    menubar.mViewFullscreen.setState(getFrame().isFullScreenMode());
                }

                //     frame(n, elapsedTime, oldTime);
                oldTime = elapsedTime;
            }
        };
        
        canvas.getRoot().addActivity(pActivity);

        if (frame != null) {
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    logger.info("WindowListener: window closed");
                    System.exit(0);
                }
            });
        }

    }

    // --------------------------------------------------------------------------
    public  String[] getScenarios() {
        //return  new String[] {"SimpleRespirator", "ArterialLine", "PkPd","CustomRespirator"};
        return new String[]{"SimpleRespirator", "CustomRespirator"};
    }

    public void setScenario(String name) {
        if (name == null) {
            name = "";
        }
        logger.info("Setting scenario; " + name);
        if (scenario != null) {
            canvas.getLayer().removeChild(scenario);
        }
        if ("ArterialLine".equals(name)) {
            scenario = new ArterialLine(this);
        } else if ("PkPd".equals(name)) {
            scenario = new PkPd(this);
        } else if ("SimpleRespirator".equals(name)) {
            scenario = new SimpleRespirator(this);
        } else {
            scenario = new CustomRespirator(this, PUtil.clock);
            initMBeanServer((CustomRespirator) scenario);
        }

        canvas.getLayer().addChild(scenario);
        viewNode(null);
    }

    public  void refreshAll() {
        menubar.setLanguage();
        statusline.repaint();
        for (Block b : scenario.getBlocks()) {
            b.refresh();
        }
        getCanvas().repaint();
        menubar.revalidate(); // don't know why this is needed twice (see menubar.setLanguage()) 
        menubar.repaint();    // don't know why this is needed twice (see menubar.setLanguage())
        status(i18n("global.welcome"));
    }

    // --------------------------------------------------------------------------
    public  void help(String s) {

    }

    // --------------------------------------------------------------------------
  /*
     * @param node  null: view all
     */
    public void viewNode(PNode node) {
       // putil.zoomTo(getCanvas().getCamera(), node, 0.02);
    }
    // --------------------------------------------------------------------------
    private static boolean highRenderingQuality;

    public  void setHighRenderingQuality(boolean flag) {
        highRenderingQuality = flag;
    }

    public boolean getHighRenderingQuality() {
        return highRenderingQuality;
    }

    public static void setRenderingQuality(Graphics2D g2) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, highRenderingQuality
                ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING, highRenderingQuality
                ? RenderingHints.VALUE_RENDER_QUALITY
                : RenderingHints.VALUE_RENDER_SPEED);

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);  // !!!!!!!!!!!!!

        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF); // !!!!!!!!!!


        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, highRenderingQuality
                ? RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
                : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, highRenderingQuality
                ? RenderingHints.VALUE_COLOR_RENDER_QUALITY
                : RenderingHints.VALUE_COLOR_RENDER_SPEED);
    }

    // --------------------------------------------------------------------------

    public  PCamera getCamera() {
        return getCanvas().getCamera();
    }

    public  double getTime() {
        return 0;
    }
    // --------------------------------------------------------------------------

    private  Locale locale;
    private static  ResourceBundle rb;

    public void setLocale(Locale loc) {
        locale = loc;
        rb = loadResourceBundle("org.jcpsim.jcpsim");
    }

    private  ResourceBundle loadResourceBundle(String name) {
        try {
            return Utf8ResourceBundle.getBundle(name, locale);
        } catch (MissingResourceException e) {
            logger.severe(e.toString());
            System.exit(0);
        }
        return null;
    }

    public  Locale getLocale() {
        return locale;
    }

    public static String i18n(String s) {
        try {
            return rb.getString(s);
        } catch (MissingResourceException e) {
            return "***" + s + "***";
        }
    }

    public  Locale[] getLanguages() {
        return new Locale[]{
                    Locale.ENGLISH,
                    Locale.GERMAN
                };
    }
    // --------------------------------------------------------------------------
    private  Scenario scenario;

    public  Scenario getScenario() {
        return scenario;
    }

    // --------------------------------------------------------------------------
    public  int getRingBufferLength() {
        return 20000;
    }

    // --------------------------------------------------------------------------
    public  void status(String s) {
        statusline.status(s);
    }
    // --------------------------------------------------------------------------
    public  boolean activated = false; // used in FrontpanelElement
    private  boolean jMXConnectorServerStarted;

    private void initMBeanServer(CustomRespirator scenario) {
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mgmntMBeanName = new ObjectName(JCpSimMgmt.OBJECT_NAME+"_"+mode);
            ObjectName clockMgmntMBeanName = new ObjectName(JCpSimClockMgmt.OBJECT_NAME+"_"+mode);

            JCpSimMgmtMBean mgmntMbean = new JCpSimMgmt(scenario);
            JCpSimClockMgmtMBean clockMgmntMbean = new JCpSimClockMgmt(PUtil.clock);

            if (mbs.isRegistered(mgmntMBeanName)) {
                mbs.unregisterMBean(mgmntMBeanName);
            }
            if (mbs.isRegistered(clockMgmntMBeanName)) {
                mbs.unregisterMBean(clockMgmntMBeanName);
            }

            mbs.registerMBean(mgmntMbean, mgmntMBeanName);
            mbs.registerMBean(clockMgmntMbean, clockMgmntMBeanName);

            if (!jMXConnectorServerStarted) {
                try{
                    java.rmi.registry.LocateRegistry.createRegistry(mode.JMXPort);
                    JMXServiceURL url = new JMXServiceURL(mode.getJMXUrl());
                    JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
                    cs.start();
                } catch(Exception e){
                   e.printStackTrace(); 
                }
                jMXConnectorServerStarted = true;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
         
    }

    // --------------------------------------------------------------------------
    /**
     * Single line log record.
     */
    public class MyFormatter extends Formatter {

        long start;
        boolean first = true;

        public String format(LogRecord rec) {
            if (first) {
                first = false;
                start = rec.getMillis();
            }
            StringBuffer buf = new StringBuffer(1000);
            buf.append(String.format("%6.2f", (float) ((rec.getMillis() - start) / 1000)));
            buf.append(' ');
            buf.append(rec.getLevel());
            buf.append(": ");
            buf.append(formatMessage(rec));
            buf.append("   (");
            buf.append(rec.getSourceClassName());
            buf.append('#');
            buf.append(rec.getSourceMethodName());
            buf.append(")\n");
            return buf.toString();
        }
    }
    // --------------------------------------------------------------------------

    public  MODE getMode() {
        return mode;
    }

    public MenuBar getMenubar() {
        return menubar;
    }

    public TopMenu getTopMenu() {
        return topMenu;
    }
    
}