/*
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-2019 BeDataDriven Groep B.V. and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, a copy is available at
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */
// Initial template generated from GraphicsDevice.h from R 3.2.2
package org.renjin.gnur.api;

import org.renjin.gcc.runtime.BytePtr;

/**
 * GNU R API methods defined in the "R_ext/GraphicsDevice.h" header file.
 *
 * <p>These are now implemented by C-language functions in the grDevices module. </p>
 */
@SuppressWarnings("unused")
public final class GraphicsDevice {

  private GraphicsDevice() { }

  @Deprecated
  public static int Rf_NumDevices() {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static void R_CheckDeviceAvailable() {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static boolean R_CheckDeviceAvailableBool() {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static int Rf_curDevice() {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static int Rf_nextDevice(int p0) {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static int Rf_prevDevice(int p0) {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static int Rf_selectDevice(int p0) {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static void Rf_killDevice(int p0) {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static int Rf_NoDevices() {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static void Rf_onintr() {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }

  @Deprecated
  public static Object Rf_AdobeSymbol2utf8(BytePtr out, BytePtr in, /*size_t*/ int nwork) {
    throw new RuntimeException("Please recompile with the latest version of Renjin.");
  }


}
