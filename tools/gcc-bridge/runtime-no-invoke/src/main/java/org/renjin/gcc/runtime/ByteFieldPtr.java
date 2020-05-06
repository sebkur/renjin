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

package org.renjin.gcc.runtime;

import java.lang.reflect.Field;

// GENERATED BY generate.groovy
// DO NOT MODIFY DIRECTLY


/**
 * Pointer to a static field of type byte
 */
public class ByteFieldPtr extends AbstractPtr {

  private final Field field;

  public static Ptr addressOf(Class declaringClass, String fieldName) {
    try {
      return new ByteFieldPtr(declaringClass.getField(fieldName));
    } catch(Exception e) {
      throw new Error(e);
    }
  }

  public ByteFieldPtr(Field field) {
    this.field = field;
  }

  @Override
  public Object getArray() {
    return field;
  }

  @Override
  public final int getOffsetInBytes() {
    return 0;
  }

  @Override
  public Ptr realloc(int newSizeInBytes) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Ptr pointerPlus(int bytes) {
    if(bytes == 0) {
      return this;
    } else {
      return new OffsetPtr(this, bytes);
    }
  }

  @Override
  public byte getByte() {
    try {
      return field.getByte(null);
    } catch (IllegalAccessException e) {
      // Should not be reachable: we compile global variables
      // to public static members
      throw new Error(e);
    }
  }

  @Override
  public void setByte(byte value) {
    try {
      field.setByte(null, value);
    } catch (IllegalAccessException e) {
      // Should not be reachable: we compile global variables
      // to public static members
      throw new Error(e);
    }
  }

  @Override
  public byte getByte(int offset) {
    if(offset == 0) {
      return getByte();
    }
    throw new IndexOutOfBoundsException();
  }


  @Override
  public void setByte(int offset, byte intValue) {
    if(offset == 0) {
      setByte(intValue);
    } else {
      throw new IndexOutOfBoundsException();
    }
  }


  @Override
  public int toInt() {
    return 0;
  }

  @Override
  public boolean isNull() {
    return false;
  }
}
