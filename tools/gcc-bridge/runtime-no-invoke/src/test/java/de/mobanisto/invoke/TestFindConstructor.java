package de.mobanisto.invoke;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static de.mobanisto.invoke.MethodHandles.publicLookup;
import static de.mobanisto.invoke.MethodType.methodType;
import static org.junit.Assert.assertEquals;

public class TestFindConstructor {

    @Test
    public void test() throws Throwable {
        MethodHandle MH_newArrayList = publicLookup().findConstructor(
                ArrayList.class, methodType(void.class, Collection.class));
        Collection orig = Arrays.asList("x", "y");
        Collection copy = (ArrayList) MH_newArrayList.invokeExact(orig);
        assert (orig != copy);
        assertEquals(orig, copy);
    }

    @Test
    public void testVarArgs() throws Throwable {
        // a variable-arity constructor:
        MethodHandle MH_newProcessBuilder = publicLookup().findConstructor(
                ProcessBuilder.class, methodType(void.class, String[].class));
        ProcessBuilder pb = (ProcessBuilder)
                MH_newProcessBuilder.invoke("x", "y", "z");
        assertEquals("[x, y, z]", pb.command().toString());
    }

}
