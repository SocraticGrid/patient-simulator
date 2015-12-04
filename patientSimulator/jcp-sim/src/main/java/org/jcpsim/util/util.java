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

import java.util.logging.Logger;


import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Various utility routines
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id$
 */
public final class util {

  private static final Logger logger = Logger.getLogger(util.class.getName());

/*
  public static Unit   pressure = new Pressure();
  public static Unit   flow     = new Flow();
*/

  private util() { }


  /**
   * Gamma filter for colors.
   * A gamma of 0.0 turns the color into white. With 1.0 there is no
   * change. Positive infinity is black. Has no effect when one
   * color component is 1.0 !!!
   * @param   c     the color to be changed
   * @param   gamma the gamma factor
   * @return        the changed color
   */
  public static Color gamma(Color c, double gamma) {
    float hsb[] = new float[3];
    Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
    hsb[2] = (float)Math.pow(hsb[2], gamma);
    return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
  }

  // --------------------------------------------------------------------------
  
  public static Color withAlpha(Color c, int alpha) {
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
  }
  
  // --------------------------------------------------------------------------
  /**
   * Fetches a string from a <code>ResourceBundle</code>.
   */
  public static String I18N(ResourceBundle rb, String s) {
    if (rb == null) {
      logger.severe(s + ": ResourceBundle == null");
      return "XXX ";
    }
    try {
      return  rb.getString(s);
    } catch(MissingResourceException e) {
      logger.severe(s + " not found in ResourceBundle " + rb.getString("title"));
      return "XXX ";
    }
  }


  /**
   * Fetches a string array from a <code>ResourceBundle</code>.
   */
  public static String[] I18Ns(ResourceBundle rb, String s) {

    StringTokenizer st  = new StringTokenizer(I18N(rb, s), "\n");
    int             n   = st.countTokens();
    String[]        str = new String[n];

    for (int i=0; i<n; i++)  str[i] = st.nextToken();
    return str;
  }


  /**
   * Outputs all elements of a  <code>ResourceBundle</code>.
   * TODO:  Sort for key;
   * TODO: Justify output.
   */
  public static void printResourceBundle(ResourceBundle rb) {
    Enumeration keys = rb.getKeys();
    while (keys.hasMoreElements()) {
      String key     = (String)keys.nextElement();
      String value[] = I18Ns(rb, key);
      logger.fine(key + ": ");
      for (int i=0; i<value.length; i++)  logger.fine(value[i] + " | ");
      logger.fine("");
    }
  }

  // more locale related stuff ????????????????????????????????????????????????

  // --------------------------------------------------------------------------

  /**
   * Returns the bigger result of a quadratic equation
   * (ax<sup><small>2</small></sup>&nbsp;+&nbsp;bx&nbsp;+&nbsp;c&nbsp;=&nbsp;0).
   */
  public static double quadEquationBigger(double a, double b, double c) {
    double d = b*b - 4*a*c;
    if (d < 0) {
      logger.severe("ERROR: quadratic equation unsolvable");
      System.exit(0);
    }
    return (-b + Math.sqrt(d)) / (2*a);
  }


  /**
   * Returns the smaller result of a quadratic equation
   * (ax<sup><small>2</small></sup>&nbsp;+&nbsp;bx&nbsp;+&nbsp;c&nbsp;=&nbsp;0).
   */
  public static double quadEquationSmaller(double a, double b, double c) {
    double d = b*b - 4*a*c;
    if (d < 0) {
      logger.severe("ERROR: quadratic equation unsolvable");
      System.exit(0);
    }
    return (-b - Math.sqrt(d)) / (2*a);
  }

  // --------------------------------------------------------------------------

  public static int sign(double x) {
    if (x > 0)  return  1;
    if (x < 0)  return -1;
                return  0;
  }

  // ------------------------------------------------------------------------

  public static void paintButtonBg(Graphics2D g2,              // no border
                            double w, double h, Color c) {
    g2.setPaint(c);
    g2.fill(new Rectangle2D.Double(0, 0, w, h));
  }


  public static void paintButtonBg(Graphics2D g2,
                            double w, double h, double m, Color c) {
    paintButtonBg(g2, w, h, m, c, c.brighter(), c.darker());
  }

  // is the painting of the border correct ???????????????????????????????????

  public static void paintButtonBg(Graphics2D g2,
                            double wd, double hd, double md,
                            Color c, Color c1, Color c2) {

    double m2 = 2.0*md;      float  w = (float)wd;
    float  h = (float)hd;    float  m = (float)md;

    g2.setPaint(c1);
    GeneralPath path = new GeneralPath();              // top
    path.moveTo(0,   0  );    path.lineTo(w,   0  );
    path.lineTo(w-m, m  );    path.lineTo(m,   m  );
    path.closePath();         g2.fill(path);
    path.reset();                                     // left
    path.moveTo(0,   0  );    path.lineTo(m,   m  );
    path.lineTo(m,   h-m);    path.lineTo(0,   h  );
    path.closePath();         g2.fill(path);
    g2.setPaint(c2);
    path.reset();                                     // left
    path.moveTo(0,   0  );    path.lineTo(m,   m  );
    path.lineTo(m,   h-m);    path.lineTo(0,   h  );
    path.closePath();         g2.fill(path);
    path.reset();                                     // bottom
    path.moveTo(m,   h-m);    path.lineTo(w-m, h-m);
    path.lineTo(w,   h  );    path.lineTo(0,   h  );
    path.closePath();         g2.fill(path);
    path.reset();                                     // right
    path.moveTo(0,   0  );    path.lineTo(m,   m  );
    path.lineTo(m,   h-m);    path.lineTo(0,   h  );
    path.closePath();         g2.fill(path);
    g2.setPaint(c);                                   // center
    g2.fill(new Rectangle2D.Double(m, m, w-m2, h-m2));
  }

  // --------------------------------------------------------------------------
  
  public enum H {
    LEFT,
    CENTER,
    RIGHT
  }

  public enum V {
    BASELINE,
    ASCENT,
    CENTER,
    DESCENT
  }


  public static void paintText(Graphics2D g2, String s, H h, V v,
                               double x, double y) {

    if (s == null)        return;
    if (s.length() == 0)  return;

    double xx = 0;
    double yy = 0;

    FontMetrics fm = g2.getFontMetrics();

    switch (h) {
      case CENTER :  xx = -fm.stringWidth(s) / 2;  break;
      case RIGHT  :  xx = -fm.stringWidth(s);      break;
    }
    switch (v) {
      case ASCENT :  yy =  fm.getAscent();         break;
      case CENTER :  yy =  fm.getAscent() / 2;     break;
      case DESCENT:  yy = -fm.getDescent();        break;
    }
    g2.drawString(s, (float)(x + xx), (float)(y + yy));
  }

  // --------------------------------------------------------------------------

  public static void paintTextLB(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.LEFT, V.BASELINE, x, y);
  }
  public static void paintTextCB(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.CENTER, V.BASELINE, x, y);
  }
  public static void paintTextRB(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.RIGHT, V.BASELINE, x, y);
  }


  public static void paintTextLA(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.LEFT, V.ASCENT, x, y);
  }
  public static void paintTextCA(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.CENTER, V.ASCENT, x, y);
  }
  public static void paintTextRA(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.RIGHT, V.ASCENT, x, y);
  }


  public static void paintTextLC(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.LEFT, V.CENTER, x, y);
  }
  public static void paintTextCC(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.CENTER, V.CENTER, x, y);
  }
  public static void paintTextRC(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.RIGHT, V.CENTER, x, y);
  }


  public static void paintTextLD(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.LEFT, V.DESCENT, x, y);
  }
  public static void paintTextCD(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.CENTER, V.DESCENT, x, y);
  }
  public static void paintTextRD(Graphics2D g2, String s, double x, double y) {
    paintText(g2, s, H.RIGHT, V.DESCENT, x, y);
  }

  // --------------------------------------------------------------------------

  public static String chars(char c, int n) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<n; i++)  sb.append(c);
    return sb.toString();
  }

  public static String lpos(int n, String s) {
    StringBuffer sb = new StringBuffer();
    sb.append(s);
    for (int i=0; i<n-s.length(); i++)  sb.append(' ');
    return sb.toString();
  }


  public static String rpos(int n, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<n-s.length(); i++)  sb.append(' ');
    sb.append(s);
    return sb.toString();
  }

  // --------------------------------------------------------------------------

  public static String getClassName(Object o) {
    String s = o.getClass().getName();
    return s.substring(s.indexOf('$')+4);
  }

  // --------------------------------------------------------------------------

  public static double log10(double x) {
    return  Math.log(x) / Math.log(10.0);
  }

  public static double fraction(double x) {
    if (x >= 0.0)  return  x - Math.floor(x);
                   return  Math.ceil(x) - x;
  }

  /**
   * Position of the decimal point.<br>
   * Negative values are treated just like positive ones.<br>
   * Zero (0.0) is handeled different!
   * @param x  The value to check.
   * @return  <ul>
              <li>if &gt;0 : the number of digits before the
                  point (123 --&gt; 3)</li>
              <li>if 0 : values (&lt; 1.0 and &gt;= 0.1) or
                                (&gt; -1.0 and &lt;= -0.1)</li>
              <li>if &li;0 : the negative number of zeros after
                  the point (0.0012 --&gt; -2)</li>
   */
  public static int dpPosition(double x) {
    if (x == 0.0)  return 0;
    return  (int)Math.floor(log10(Math.abs(x))) + 1;
  }

  /**
   * Calculates the number digits before the decimal point
   * (including the minus sign).
   */
  public static int integerNeeded(double x) {
    int i = Math.max(1, dpPosition(x));
    if (x < -0.0)  i++;
    return i;
  }

  /**
   * TODO: This is not very elegant.
   */
  public static int fractionNeeded(double x) {
    double y = Math.abs(x);
    int n = 0;
    do {
      if (y / (int)y < 1.001)  return n;
      n++;
      y *= 10.0;
    } while (true);
  }

  // --------------------------------------------------------------------------

  /**
   * @param  x
   * @param  integer
   * @param  fraction
   * @return The formatted number.
   *
   * TODO:   No leading zeroes.
   * TODO:   Minus sign counts as part of integer.
   * TODO:   Test it.
   * TODO:   Parameter boolean fill (leading spaces).
   * TODO:   Parameter int align.
   */
  public static String fnum(double x, int integer, int fraction) {

    NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
    nf.setMaximumIntegerDigits(integer);
    nf.setMinimumIntegerDigits(1);
    nf.setMaximumFractionDigits(fraction);
    nf.setMinimumFractionDigits(fraction);
    nf.setGroupingUsed(false);

    return nf.format(x);
  }

  // --------------------------------------------------------------------------

  public static String format(double value, double min, double max,
                              double step) {

    int integer  = Math.max(integerNeeded(min), integerNeeded(max));
    int fraction = fractionNeeded(step);

    return fnum(value, integer, fraction);
  }

  // --------------------------------------------------------------------------

  public int clipToByte(int x) {
    if (x > 255)  return 255;
    if (x <   0)  return   0;
                  return x;
  }

  // -------------------------------------------------------------------------

  public int toRGB(double r, double g, double b) {
    return  (clipToByte((int)(255.0 * r))<<16) |
            (clipToByte((int)(255.0 * g))<< 8) |
             clipToByte((int)(255.0 * b));
  }

  // -------------------------------------------------------------------------

  public static Document getXmlDocument(File file) {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      return  builder.parse(file);
    } catch (SAXException sxe) {
      Exception  x = sxe;
      if (sxe.getException() != null)  x = sxe.getException();
      x.printStackTrace();
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return null;
  }

  // --------------------------------------------------------------------------

  public static Document getXmlDocument(InputStream is) {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      return  builder.parse(is);
    } catch (SAXException sxe) {
      Exception  x = sxe;
      if (sxe.getException() != null)  x = sxe.getException();
      x.printStackTrace();
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return null;
  }
  // --------------------------------------------------------------------------

  public static Document getNewDocument() {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.newDocument();
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    }
    return null;
  }

  // --------------------------------------------------------------------------

  public static void writeXmlFile(Document document, File file,
                                  boolean indent) {
    try {
      Transformer t = TransformerFactory.newInstance().newTransformer();
      if (indent)  t.setOutputProperty("indent", "yes");
      t.transform(new DOMSource(document), new StreamResult(file));
    } catch (TransformerConfigurationException e) {
      logger.severe(e.toString());
      System.exit(1);
    } catch (TransformerException e) {
      logger.severe(e.toString());
      System.exit(1);
    }
  }

  // --------------------------------------------------------------------------
  /**
   * Returns the (lowercase) extension of the name of the file.
   */
  public static String getExtension(File f) {
    String s = f.getName();
    int    i = s.lastIndexOf('.');
    if (i>0 &&  i<s.length()-1)  return s.substring(i+1).toLowerCase();
    return null;
  }

  // --------------------------------------------------------------------------

  public static URL[] getJarURLs(String directory) {

//  logger.info(directory);
    
    File dir = new File(directory);
    File f[] = dir.listFiles(new FileFilter() {
      public boolean accept(File f) { return f.getName().endsWith(".jar"); }
    });
    URL u[] = new URL[f.length];
    for (int i=0; i<f.length; i++) {
      try {
        u[i] = f[i].toURI().toURL();
      } catch(MalformedURLException e) {
        logger.severe(f[i] + " --> " + e);
      }
    }
    return u;
  }

  // --------------------------------------------------------------------------
  /**
   * @param frame the frame that schall be resized and centered on the screen
   * @param ratio 0.5: half width and half height of screen
   */
  public static void centerFrame(JFrame frame, double ratio) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setBounds((int)(screen.width  * (1-ratio)/2),
                    (int)(screen.height * (1-ratio)/2),
                    (int)(screen.width  * ratio),
                    (int)(screen.height * ratio));
    frame.setVisible(true);
  }
}
