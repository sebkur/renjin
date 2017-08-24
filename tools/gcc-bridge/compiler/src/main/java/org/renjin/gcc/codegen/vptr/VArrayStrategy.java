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

package org.renjin.gcc.codegen.vptr;

import org.renjin.gcc.codegen.MethodGenerator;
import org.renjin.gcc.codegen.array.ArrayTypeStrategy;
import org.renjin.gcc.codegen.expr.*;
import org.renjin.gcc.codegen.fatptr.ValueFunction;
import org.renjin.gcc.codegen.type.*;
import org.renjin.gcc.codegen.var.VarAllocator;
import org.renjin.gcc.gimple.GimpleVarDecl;
import org.renjin.gcc.gimple.expr.GimpleConstructor;
import org.renjin.gcc.gimple.type.GimpleArrayType;
import org.renjin.gcc.runtime.Ptr;
import org.renjin.repackaged.asm.Type;

import java.lang.reflect.Field;

public class VArrayStrategy implements TypeStrategy<VArrayExpr> {

  private GimpleArrayType arrayType;

  public VArrayStrategy(GimpleArrayType arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public ParamStrategy getParamStrategy() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public ReturnStrategy getReturnStrategy() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public ValueFunction getValueFunction() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public VArrayExpr variable(GimpleVarDecl decl, VarAllocator allocator) {
    PointerType pointerType = PointerType.ofType(arrayType.getComponentType());
    JExpr malloc = VPtrStrategy.malloc(pointerType, Expressions.constantInt(arrayType.sizeOf())).getRef();
    JLValue var = allocator.reserve(decl.getName(), Type.getType(Ptr.class), malloc);

    return new VArrayExpr(arrayType, new VPtrExpr(var));
  }

  @Override
  public VArrayExpr providedGlobalVariable(GimpleVarDecl decl, Field javaField) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public VArrayExpr constructorExpr(ExprFactory exprFactory, MethodGenerator mv, GimpleConstructor value) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public FieldStrategy fieldGenerator(Type className, String fieldName) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public FieldStrategy addressableFieldGenerator(Type className, String fieldName) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public PointerTypeStrategy pointerTo() {
    return new VPtrStrategy(arrayType);
  }

  @Override
  public VArrayStrategy arrayOf(GimpleArrayType arrayType) {
    return new VArrayStrategy(arrayType);
  }

  @Override
  public VArrayExpr cast(MethodGenerator mv, GExpr value, TypeStrategy typeStrategy) throws UnsupportedCastException {
    throw new UnsupportedOperationException("TODO");
  }
}
