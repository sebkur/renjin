/*
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-${$file.lastModified.year} BeDataDriven Groep B.V. and contributors
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

package org.renjin.primitives.special;

import org.renjin.eval.Context;
import org.renjin.eval.EvalException;
import org.renjin.sexp.*;

/**
 * The logical && operator requires the implementation use minimal evaluation,
 * therefore we cannot use the overloaded function calls as is standard.
 * <p>
 * Comparing doubles or booleans works as generally expected. Comparing two vectors
 * will only compare the first element in each vector.
 */
public class AndFunction extends SpecialFunction {

  public AndFunction() {
    super("&&");
  }


  @Override
  public SEXP apply(Context context, Environment rho, FunctionCall call) {
    checkArity(call, 2);
    int x = test(context.evaluate(call.getArgument(0), rho), "invalid 'x' type in 'x && y'");

    if (x == 0) {
      return LogicalVector.FALSE;
    }

    int y = test(context.evaluate(call.getArgument(1), rho), "invalid 'y' type in 'x && y'");

    if (y == 0) {
      return LogicalVector.FALSE;
    } else if(x == IntVector.NA || y == IntVector.NA) {
      return LogicalVector.NA_VECTOR;
    } else {
      return LogicalVector.TRUE;
    }
  }

  public static int test(SEXP exp, String errorMessage) {
    if (exp instanceof AtomicVector && !(exp instanceof StringVector)) {
      AtomicVector vector = (AtomicVector) exp;
      if (vector.length() == 0) {
        return LogicalVector.NA;
      } else {
        return vector.getElementAsRawLogical(0);
      }
    }
    throw new EvalException(errorMessage);
  }

}
