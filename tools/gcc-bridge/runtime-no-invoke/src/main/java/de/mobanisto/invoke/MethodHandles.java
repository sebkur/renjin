package de.mobanisto.invoke;

public class MethodHandles {

    public static final class Lookup {

        public MethodHandle findConstructor(Class<?> refc,
                                            MethodType type)
                throws NoSuchMethodException, IllegalAccessException {
            return null;
        }

        public MethodHandle findStatic(Class<?> refc,
                                       String name,
                                       MethodType type)
                throws NoSuchMethodException, IllegalAccessException {
            return null;
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
