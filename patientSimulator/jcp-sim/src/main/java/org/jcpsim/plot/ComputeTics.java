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

import java.text.NumberFormat;
import java.util.Locale;


/**
 * Finds the optimal scale for one axis
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 */
public class ComputeTics {

  static final long serialVersionUID = 0L;

  public double ticmin;      // lowest tic (&lt;= omin).
  public double ticmax;      // highest tic (&gt;= omax).
  public double ticd;        // value difference between two adjacent tics
  public int    tics;        // number of tics
  public int    frac;        // position of fraction point of numbers

  private NumberFormat nf;
  
  /**
   * @param omin   lowest desired value
   * @param omax   highest desired value
   * @param catics approximate desired number of tics
   */
  public ComputeTics(double omin, double omax, double catics) {

    double delta[] = { 0.5, 1, 2, 2.5, 5, 10 };
    int    pnt[]   = {   1, 0, 0,   1, 0,  0 };

    if (catics < 1)  catics = 1;

    double abst = (omax-omin) / catics;
    int    logd = (int) Math.floor(Math.log(abst) / Math.log(10));
    double expo = Math.pow(10, logd);
    double mant = abst / expo;

    int i = -1;
    while (mant > delta[++i]) ;

    ticd   = delta[i-1] * expo;
    ticmin = -Math.floor(-omin/ticd) * ticd;
    ticmax =       (int)( omax/ticd) * ticd;
    tics   = (int)((ticmax-ticmin) / ticd);

    frac   = Math.max(0, pnt[i-1] - logd);
    
    nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
    nf.setMaximumIntegerDigits(8);
    nf.setMinimumIntegerDigits(1);
    nf.setMaximumFractionDigits(frac);
    nf.setMinimumFractionDigits(frac);
  }
  
  public String format(double x) {
    return nf.format(x); 
  }
  
  public boolean isZero(double i) {
    return (Math.abs(i) < ticd/10.0);
  }
}
