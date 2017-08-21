/*
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-${year} BeDataDriven Groep B.V. and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, a copy is available at
 *  https://www.gnu.org/licenses/gpl-2.0.txt
 *
 */

package org.renjin.gcc.runtime;

/**
 * A pointer to one or more pointers.
 */
public class PointerPtr extends AbstractPtr {

  private static final int BYTES = 4;

  public static final PointerPtr NULL = new PointerPtr(null, 0);

  /**
   * An array of pointers. We consider pointers to be 32-bits.
   */
  private Object[] array;

  /**
   * Offset from the beginning of the pointer in bytes.
   */
  private int offset;

  public PointerPtr(Object[] array) {
    this.array = array;
  }

  /**
   *
   * @param array the storage array
   * @param offset the offset in bytes from the start of the array.
   */
  public PointerPtr(Object[] array, int offset) {
    checkAligned(offset);
    this.array = array;
    this.offset = offset;
  }

  public static PointerPtr malloc(int bytes) {
    return new PointerPtr(new Object[mallocSize(bytes, BYTES)], 0);
  }

  @Override
  public Ptr realloc(int newSizeInBytes) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Ptr pointerPlus(int bytes) {
    return new PointerPtr(array, this.offset + bytes);
  }

  @Override
  public byte getByte(int offset) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public void setByte(int offset, byte value) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Ptr getPointer(int offset) {
    return (Ptr) array[ checkAligned(this.offset + offset) ];
  }

  @Override
  public void setPointer(int offset, Ptr value) {
    array[ checkAligned(this.offset + offset) ] = value;
  }

  private static int checkAligned(int bytes) {
    if(bytes % BYTES != 0) {
      throw new UnsupportedOperationException("Unaligned access");
    }
    return bytes / BYTES;
  }

  @Override
  public int toInt() {
    return offset;
  }

  @Override
  public boolean isNull() {
    return array == null && offset == 0;
  }
}
