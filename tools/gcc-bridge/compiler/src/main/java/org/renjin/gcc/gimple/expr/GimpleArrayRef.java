package org.renjin.gcc.gimple.expr;

import com.google.common.base.Predicate;
import org.renjin.gcc.gimple.type.GimpleArrayType;
import org.renjin.gcc.gimple.type.GimpleIntegerType;

import java.util.List;

public class GimpleArrayRef extends GimpleLValue {
  private GimpleExpr array;
  private GimpleExpr index;

  public GimpleArrayRef() {
  }
  
  public GimpleArrayRef(GimpleExpr array, int index) {
    this.array = array;
    this.index = new GimpleIntegerConstant(GimpleIntegerType.unsigned(32), index);
    this.setType(((GimpleArrayType) array.getType()).getComponentType());
  }

  public GimpleExpr getArray() {
    return array;
  }

  public void setValue(GimpleExpr value) {
    this.array = value;
  }

  public void setIndex(GimpleExpr index) {
    this.index = index;
  }

  public GimpleExpr getIndex() {
    return index;
  }

  @Override
  public void find(Predicate<? super GimpleExpr> predicate, List<GimpleExpr> results) {
    findOrDescend(array, predicate, results);
    findOrDescend(index, predicate, results);
  }

  @Override
  public boolean replace(Predicate<? super GimpleExpr> predicate, GimpleExpr replacement) {
    if(predicate.apply(array)) {
      array = replacement;
      return true;
    } else if(predicate.apply(index)) {
      index = replacement;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return array + "[" + index + "]";
  }
}
