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
package org.jcpsim.ode;

/**
 * Tasks which have to be performed before and after the
 * differential equations
 *
 * @author  Dr. Frank Fischer &lt;frank@jcpsim.org&gt;
 * @version CVS $Id: Tasks.java,v 1.1 2006/08/22 23:32:51 jcpsim Exp $
 * TODO:    is before and after ever used ???
 * TODO:    it is probably simpler and faster if timeStep and macroTimeStep
 *          (new names) can be registered seperately
 */
public class Tasks {

  /**
   *
   */
  public void before(double t) { }
  /**
   *
   */
  public void after(double t) { }
  /**
   * The step just taken was small enough to achieve sufficient
   * accurate integration of the differential equations.
   * @param t      The time that was reached by the time step (not neccessarily
   *               'real time' !!!).
   * @param dtused The time step that previously was used
   */
  public void afterTimeStep(double t, double dtused) { }
  /**
   * The step just taken was small enough to achieve smooth
   * animations (usually smaller than 0.1 second).
   * @param t  The time that was reached by the time step (not neccessarily
   *           'real time' !!!).
   */
  public void afterMacroTimeStep(double t) { }
}