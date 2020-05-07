package de.mobanisto.invoke;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static de.mobanisto.invoke.MethodHandles.publicLookup;
import static de.mobanisto.invoke.MethodType.methodType;
import static org.junit.Assert.assertEquals;

public class TestFindStatic {

    @Test
    public void test() throws Throwable {
        MethodHandle MH_asList = publicLookup().findStatic(Arrays.class,
                "copyOf", methodType(byte[].class, byte[].class, int.class));
        byte[] original = new byte[]{1, 2, 3};
        Object result = MH_asList.invoke(original, 3);
        assertEquals("1, 2, 3", toString((byte[]) result));
    }

    private String toString(byte[] array) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            buffer.append(array[i]);
            if (i < array.length - 1) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    @Test
    public void testVarArgs() throws Throwable {
        MethodHandle MH_asList = publicLookup().findStatic(Arrays.class,
                "asList", methodType(List.class, Object[].class));
        assertEquals("[x, y]", MH_asList.invoke("x", "y").toString());
    }

}
