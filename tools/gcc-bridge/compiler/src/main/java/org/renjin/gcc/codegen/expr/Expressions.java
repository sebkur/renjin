package org.renjin.gcc.codegen.expr;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.objectweb.asm.Type;
import org.renjin.gcc.codegen.MethodGenerator;
import org.renjin.gcc.codegen.WrapperType;
import org.renjin.gcc.codegen.type.primitive.ConstantValue;
import org.renjin.gcc.codegen.type.primitive.op.PrimitiveBinOpGenerator;
import org.renjin.gcc.gimple.GimpleOp;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Type.getMethodDescriptor;

/**
 * Static utility methods pertaining to create and compose {@link GExpr}s
 */
public class Expressions {

  public static JExpr newArray(final Type componentType, final int length) {
    Preconditions.checkArgument(length >= 0);
    return newArray(componentType, constantInt(length));
  }

  public static JExpr newArray(final WrapperType componentType, final int length) {
    return newArray(componentType.getWrapperType(), length);
  }

  public static JExpr newArray(Class<?> componentClass, int length) {
    return newArray(Type.getType(componentClass), length);
  }
  
  public static JExpr newArray(final Type componentType, final JExpr length) {
    checkType("length", length, Type.INT);
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return Type.getType("[" + componentType.getDescriptor());
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        length.load(mv);
        mv.newarray(componentType);
      }
    };
  }
  
  public static JExpr newArray(JExpr value, final JExpr... moreValues) {

    List<JExpr> values = new ArrayList<>();
    values.add(value);
    values.addAll(Arrays.asList(moreValues));
      
    return newArray(value.getType(), values);
  }

  public static JExpr newArray(final Type componentType, final List<JExpr> values) {
    return newArray(componentType, values.size(), values);
  }


  public static JExpr newArray(Type valueType, int elementLength, Optional<JExpr> firstValue) {
    List<JExpr> initialValues = Lists.newArrayList();
    if(firstValue.isPresent()) {
      initialValues.add(firstValue.get());
    }
    return newArray(valueType, elementLength, initialValues);
  }

  public static JExpr newArray(final Type componentType, final int arrayLength, final List<JExpr> values) {
    Preconditions.checkNotNull(componentType, "componentType");
    
    if(values.size() > arrayLength) {
      throw new IllegalArgumentException(
          String.format("Number of initial values supplied (%d) is greater than array length (%d)",
              values.size(),
              arrayLength));
    }
    final Type arrayType = Type.getType("[" + componentType.getDescriptor());

    // check the types now
    for (int i = 0; i < values.size(); i++) {
      Type elementType = values.get(i).getType();
      if(elementType.getSort() != componentType.getSort()) {
        throw new IllegalArgumentException(String.format("Invalid type at element %d: %s, expected %s",
            i, elementType, componentType));
      }
    }

    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return arrayType;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        mv.iconst(arrayLength);
        mv.newarray(componentType);
        for (int i = 0; i < values.size(); i++) {
          mv.dup();
          mv.iconst(i);
          values.get(i).load(mv);
          mv.astore(componentType);
        }
      }
    };
  }


  public static ArrayElement elementAt(JExpr array, final int offset) {
    return elementAt(array, constantInt(offset));
  }

  public static ArrayElement elementAt(final JExpr array, final JExpr offset) {
    checkType("array", array, Type.ARRAY);
    checkType("offset", offset, Type.INT_TYPE);
    
    return new ArrayElement(array, offset);
  }

  public static JExpr nullRef(final Type type) {
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return type;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        mv.aconst(null);
      }
    };
  }

  public static JExpr constantInt(final int value) {
    return new ConstantValue(Type.INT_TYPE, value);
  }

  public static JExpr zero() {
    return constantInt(0);
  }

  public static JExpr zero(final Type type) {
    return new ConstantValue(type, 0);
  }

  public static JExpr sum(final JExpr x, final JExpr y) {
    return new PrimitiveBinOpGenerator(GimpleOp.PLUS_EXPR, x, y);
  }
  
  public static JExpr sum(JExpr x, int y) {
    if(y == 0) {
      return x;
    } else {
      return sum(x, constantInt(y));
    }
  }

  public static JExpr difference(JExpr x, JExpr y) {
    return new PrimitiveBinOpGenerator(GimpleOp.MINUS_EXPR, x, y);
  }

  public static JExpr difference(JExpr x, int y) {
    if(y == 0) {
      return x;
    } else {
      return difference(x, constantInt(y));
    }
  }

  public static JExpr product(JExpr x, JExpr y) {
    return new PrimitiveBinOpGenerator(GimpleOp.MULT_EXPR, x, y);
  }
  
  public static JExpr product(JExpr x, int y) {
    Preconditions.checkArgument(x.getType().equals(Type.INT_TYPE));
    
    if(y == 0) {
      return zero(x.getType());
    } else if(y == 1) {
      return x;
    } else {
      return product(x, constantInt(y));
    }
  }

  public static JExpr divide(JExpr x, JExpr y) {
    return new PrimitiveBinOpGenerator(GimpleOp.EXACT_DIV_EXPR, x, y);
  }

  public static JExpr divide(JExpr size, int divisor) {
    Preconditions.checkArgument(size.getType().equals(Type.INT_TYPE));

    return divide(size, constantInt(divisor));
  }

  public static JLValue field(final JExpr instance, final Type fieldType, final String fieldName) {
    checkType("instance", instance, Type.OBJECT);
    
    return new JLValue() {
      @Nonnull
      @Override
      public Type getType() {
        return fieldType;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        instance.load(mv);
        mv.getfield(instance.getType().getInternalName(), fieldName, fieldType.getDescriptor());
      }

      @Override
      public void store(MethodGenerator mv, JExpr value) {
        instance.load(mv);
        value.load(mv);
        mv.putfield(instance.getType().getInternalName(), fieldName, fieldType.getDescriptor());
      }
    };
  }
  
  private static void checkType(String argName, JExpr value, int expectedSort) {
    if(value.getType().getSort() != expectedSort) {
      throw new IllegalArgumentException(String.format("Illegal type for %s: %s", argName, value.getType()));
    }
  }
  private static void checkType(String argName, JExpr value, Type expectedType) {
    if(!value.getType().equals(expectedType)) {
      throw new IllegalArgumentException(String.format("Illegal type %s for %s: Expected %s",
          value.getType(), argName, expectedType));
    }
  }

  public static JExpr cast(final JExpr object, final Type type) {
    
    // Can we reduce this to a NOOP ?
    if(object.getType().equals(type) || type.equals(Type.getType(Object.class))) {
      return object;
    }
    
    // Verify that this is in the realm of possibility
    checkCast(object.getType(), type);

    return uncheckedCast(object, type);
  }

  public static JExpr uncheckedCast(final JExpr object, final Type type) {
    return new JLValue() {

      @Nonnull
      @Override
      public Type getType() {
        return type;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        object.load(mv);
        mv.checkcast(type);
      }
      
      @Override
      @SuppressWarnings("unchecked")
      public void store(MethodGenerator mv, JExpr value) {
        if(!(object instanceof JLValue)) {
          throw new UnsupportedOperationException();
        }
        ((JLValue) object).store(mv, value);
      }
    };
  }

  public static JExpr castPrimitive(final JExpr expr, final Type type) {
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return type;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        expr.load(mv);
        mv.cast(expr.getType(), type);
      }
    };
  }

  private static void checkCast(Type fromType, Type toType) {

    if (fromType.equals(toType)) {
      return;
    }
    
    if (toType.getSort() != Type.OBJECT && 
        toType.getSort() != Type.ARRAY) {
      throw new IllegalArgumentException("Target type for cast must be an array or object: " + toType);
    }
    int fromSort = fromType.getSort();
    int toSort = toType.getSort();
    if(fromSort != toSort) {
      throw new IllegalArgumentException("Invalid cast from " + fromType + " to " + toType);
    }
    if(fromSort == Type.ARRAY) {
      checkCast(fromType.getElementType(), toType.getElementType());
    }
  }

  public static JExpr voidValue() {
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return Type.VOID_TYPE;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        // LOAD NOTHING
      }
    };
  }

  public static JExpr thisValue(final Type type) {
    return new JExpr() {

      @Nonnull
      @Override
      public Type getType() {
        return type;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        mv.load(0, type);
      }
    };
  }

  public static boolean isPrimitive(JExpr simpleExpr) {
    return isPrimitive(simpleExpr.getType());
  }

  public static boolean isPrimitive(Type type) {
    switch (type.getSort()) {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.SHORT:
      case Type.CHAR:
      case Type.INT:
      case Type.LONG:
      case Type.FLOAT:
      case Type.DOUBLE:
        return true;
      
      case Type.OBJECT:
      case Type.ARRAY:
      case Type.METHOD:
        return false;
     
      default:
        throw new IllegalArgumentException("type: " + type);
    }
  }

  public static JExpr box(final JExpr simpleExpr) {
    Preconditions.checkArgument(isPrimitive(simpleExpr), "simpleExpr must be a primitive");
    
    Type primitiveType = simpleExpr.getType();
    final Type boxedType = boxedType(primitiveType);
    final String valueOfDescriptor = getMethodDescriptor(boxedType, primitiveType);
    
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return boxedType;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        simpleExpr.load(mv);
        mv.invokestatic(boxedType.getInternalName(), "valueOf", valueOfDescriptor, false);    
      }
    };
  }
  
  public static Type boxedType(Type type) {
    switch (type.getSort()) {
      case Type.BOOLEAN:
        return Type.getType(Boolean.class);
      case Type.BYTE:
        return Type.getType(Byte.class);
      case Type.SHORT:
        return Type.getType(Short.class);
      case Type.CHAR:
        return Type.getType(Character.class);
      case Type.INT:
        return Type.getType(Integer.class);
      case Type.LONG:
        return Type.getType(Long.class);
      case Type.FLOAT:
        return Type.getType(Float.class);
      case Type.DOUBLE:
        return Type.getType(Double.class);
      
      default:
        throw new IllegalArgumentException("type: " + type);
    }
  }

  public static JExpr copyOfArrayRange(final JExpr array, final JExpr from, final JExpr to) {

    final Type arrayType = array.getType();

    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return arrayType;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        // static byte[] copyOfRange(byte[] original, int from, int to);
        
        array.load(mv);
        from.load(mv);
        to.load(mv);
        mv.invokestatic(Arrays.class, "copyOfRange", 
            getMethodDescriptor(arrayType, arrayType, Type.INT_TYPE, Type.INT_TYPE));
      }
    };
  }

  public static JExpr copyOfArray(final JExpr array) {

    final Type arrayType = array.getType();

    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return arrayType;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        // public static char[] copyOf(char[] original, int newLength) {

        array.load(mv);
        mv.dup();
        mv.arraylength();
        mv.invokestatic(Arrays.class, "copyOf",
            getMethodDescriptor(arrayType, arrayType, Type.INT_TYPE));
      }
    };
  }

  public static JExpr newObject(final Type classType) {
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return classType;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        mv.anew(classType);
        mv.dup();
        mv.invokeconstructor(classType);
      }
    };
  }
  
  public static JExpr shiftRight(final JExpr x, int bits) {
    if(bits == 0) {
      return x;
    }
    return shiftRight(x, constantInt(bits));
  }
  
  public static JExpr shiftRight(final JExpr x, final JExpr bits) {
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return x.getType();
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        x.load(mv);
        bits.load(mv);
        mv.shr(x.getType());
      }
    };
  }


  public static JLValue localVariable(final Type type, final int index) {
    return new JLValue() {

      @Nonnull
      @Override
      public Type getType() {
        return type;
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        mv.load(index, type);
      }

      @Override
      public void store(MethodGenerator mv, JExpr rhs) {
        rhs.load(mv);
        mv.store(index, type);
      }
    };
  }

  public static JExpr identityHash(final JExpr value) {
    if (value.getType().getSort() != Type.OBJECT &&
        value.getType().getSort() != Type.ARRAY) {
      throw new IllegalArgumentException("value must have a reference type: " + value.getType());
    }
    
    return staticMethodCall(System.class, "identityHashCode", 
        getMethodDescriptor(Type.INT_TYPE, Type.getType(Object.class)), value);
  }
  
  public static JExpr numberOfLeadingZeros(JExpr value) {
    checkType("value", value, Type.INT_TYPE);
    
    return staticMethodCall(Type.getType(Integer.class), "numberOfLeadingZeros", 
        getMethodDescriptor(Type.INT_TYPE, Type.INT_TYPE), value);
  }

  public static JExpr staticMethodCall(final Class declaringType, final String methodName,
                                       final String descriptor, final JExpr... arguments) {
    return staticMethodCall(Type.getType(declaringType), methodName, descriptor, arguments);
  }
  
  public static JExpr staticMethodCall(final Type declaringType, final String methodName, 
                                       final String descriptor, final JExpr... arguments) {
    return new JExpr() {
      @Nonnull
      @Override
      public Type getType() {
        return Type.getReturnType(descriptor);
      }

      @Override
      public void load(@Nonnull MethodGenerator mv) {
        for (JExpr argument : arguments) {
          argument.load(mv);
        }
        mv.invokestatic(declaringType, methodName, descriptor);
      }
    };
  }
}