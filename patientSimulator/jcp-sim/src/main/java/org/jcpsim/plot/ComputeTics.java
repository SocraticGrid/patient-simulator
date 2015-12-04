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
