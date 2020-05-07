package de.mobanisto.invoke;

public class MethodType {

    private Class<?> rtype;
    private Class<?> ptypes[];

    public MethodType(Class<?> rtype) {
        this.rtype = rtype;
    }

    public MethodType(Class<?> rtype, Class<?> ptype0) {
        this.rtype = rtype;
        this.ptypes = new Class<?>[]{ptype0};
    }

    public MethodType(Class<?> rtype, Class<?>... ptypes) {
        this.rtype = rtype;
        this.ptypes = ptypes;
    }

    public static MethodType methodType(Class<?> rtype) {
        return new MethodType(rtype);
    }

    public static MethodType methodType(Class<?> rtype, Class<?> ptype0) {
        return new MethodType(rtype, ptype0);
    }

    public static MethodType methodType(Class<?> rtype, Class<?>... ptypes) {
        return new MethodType(rtype, ptypes);
    }

    Class<?>[] getParametersAsArray() {
        return ptypes;
    }

}
