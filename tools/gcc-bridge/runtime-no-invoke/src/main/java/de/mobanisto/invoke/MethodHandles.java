package de.mobanisto.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MethodHandles {

    public static final class Lookup {

        public MethodHandle findConstructor(Class<?> refc,
                                            MethodType type)
                throws NoSuchMethodException, IllegalAccessException {
            Constructor<?> constructor = refc.getDeclaredConstructor(type.getParametersAsArray());
            return new MethodHandle(constructor);
        }

        public MethodHandle findStatic(Class<?> refc,
                                       String name,
                                       MethodType type)
                throws NoSuchMethodException, IllegalAccessException {
            Method method = refc.getDeclaredMethod(name, type.getParametersAsArray());
            return new MethodHandle(method);
        }

    }

    public static Lookup publicLookup() {
        return new Lookup();
    }

    public static MethodHandle insertArguments(MethodHandle target, int pos, Object... values) {
        return null;
    }

    public static MethodHandle throwException(Class<?> returnType, Class<? extends Throwable> exType) {
        return null;
    }

    public static MethodHandle foldArguments(MethodHandle target, MethodHandle combiner) {
        return null;
    }

}
