/**
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-2016 BeDataDriven Groep B.V. and contributors
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
package org.renjin.gcc.codegen.type.voidt;

import org.renjin.gcc.codegen.MethodGenerator;
import org.renjin.gcc.codegen.array.FatArrayExpr;
import org.renjin.gcc.codegen.condition.ConditionGenerator;
import org.renjin.gcc.codegen.expr.*;
import org.renjin.gcc.codegen.fatptr.FatPtr;
import org.renjin.gcc.codegen.fatptr.FatPtrPair;
import org.renjin.gcc.codegen.fatptr.ValueFunction;
import org.renjin.gcc.codegen.fatptr.Wrappers;
import org.renjin.gcc.codegen.type.UnsupportedCastException;
import org.renjin.gcc.codegen.type.fun.FunPtr;
import org.renjin.gcc.codegen.type.primitive.PrimitiveValue;
import org.renjin.gcc.codegen.type.record.RecordArrayExpr;
import org.renjin.gcc.codegen.type.record.RecordLayout;
import org.renjin.gcc.codegen.type.record.unit.RecordUnitPtr;
import org.renjin.gcc.codegen.vptr.VPtrExpr;
import org.renjin.gcc.codegen.vptr.VPtrRecordExpr;
import org.renjin.gcc.gimple.GimpleOp;
import org.renjin.gcc.gimple.type.GimplePrimitiveType;
import org.renjin.gcc.gimple.type.GimpleType;
import org.renjin.gcc.runtime.Ptr;
import org.renjin.repackaged.asm.Label;
import org.renjin.repackaged.asm.Type;

import java.lang.invoke.MethodHandle;


public class VoidPtrExpr implements RefPtrExpr {
  
  private JExpr objectRef;
  private FatPtr address;

  public VoidPtrExpr(JExpr objectRef, FatPtr address) {
    this.objectRef = objectRef;
    this.address = address;
  }

  public VoidPtrExpr(JExpr objectRef) {
    this.objectRef = objectRef;
    this.address = null;
  }

  @Override
  public void store(MethodGenerator mv, GExpr rhs) {
    JLValue lhs = (JLValue) this.objectRef;

    if(rhs instanceof FatPtr) {
      FatPtr fatPtrExpr = (FatPtr) rhs;
      lhs.store(mv, fatPtrExpr.wrap());
    } else {
      lhs.store(mv, ((RefPtrExpr) rhs).unwrap());
    }
  }
  
  @Override
  public GExpr addressOf() {
    if(address == null) {
      throw new NotAddressableException();
    }
    return address;
  }

  @Override
  public FunPtr toFunPtr() {
    return new FunPtr(Expressions.cast(objectRef, Type.getType(MethodHandle.class)));
  }

  @Override
  public FatArrayExpr toArrayExpr() throws UnsupportedCastException {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public PrimitiveValue toPrimitiveExpr(GimplePrimitiveType targetType) throws UnsupportedCastException {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public JExpr unwrap() {
    return objectRef;
  }

  @Override
  public void jumpIfNull(MethodGenerator mv, Label label) {
    objectRef.load(mv);
    mv.ifnull(label);
  }

  @Override
  public JExpr memoryCompare(MethodGenerator mv, PtrExpr otherPointer, JExpr n) {
    return new VoidPtrMemCmp(unwrap(), otherPointer.toVoidPtrExpr().unwrap(), n);
  }

  @Override
  public void memorySet(MethodGenerator mv, JExpr byteValue, JExpr length) {
    objectRef.load(mv);
    byteValue.load(mv);
    length.load(mv);

    mv.invokestatic(org.renjin.gcc.runtime.VoidPtr.class, "memset",
        Type.getMethodDescriptor(Type.VOID_TYPE,
            Type.getType(Object.class),
            Type.INT_TYPE,
            Type.INT_TYPE));
  }

  @Override
  public PtrExpr realloc(MethodGenerator mv, JExpr newSizeInBytes) {
    return new VoidPtrExpr(new VoidPtrRealloc(unwrap(), newSizeInBytes));
  }

  @Override
  public GExpr valueOf(GimpleType expectedType) {
    throw new UnsupportedOperationException("void pointers cannot be dereferenced.");
  }

  @Override
  public ConditionGenerator comparePointer(MethodGenerator mv, GimpleOp op, GExpr otherPointer) {
    return new VoidPtrComparison(op, unwrap(), otherPointer.toVoidPtrExpr().unwrap());
  }

  @Override
  public VoidPtrExpr toVoidPtrExpr() throws UnsupportedCastException {
    return this;
  }

  @Override
  public RecordArrayExpr toRecordArrayExpr() throws UnsupportedCastException {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public VPtrExpr toVPtrExpr() throws UnsupportedCastException {
    return new VPtrExpr(Expressions.cast(objectRef, Type.getType(Ptr.class)));
  }

  @Override
  public RecordUnitPtr toRecordUnitPtrExpr(RecordLayout layout) {
    return new RecordUnitPtr(layout, Expressions.cast(unwrap(), layout.getType()));
  }

  @Override
  public FatPtr toFatPtrExpr(ValueFunction valueFunction) {
    JExpr wrapperInstance = Wrappers.cast(valueFunction.getValueType(), objectRef);
    JExpr arrayField = Wrappers.arrayField(wrapperInstance);
    JExpr offsetField = Wrappers.offsetField(wrapperInstance);

    return new FatPtrPair(valueFunction, arrayField, offsetField);
  }

  @Override
  public VPtrRecordExpr toVPtrRecord() {
    throw new UnsupportedOperationException("TODO");
  }
}
