package com.pixurvival.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

class IndexSetTest {

    @Test
    public void regularIdSetTest() {
        testIdSet(IndexSet.RegularIndexSet.class,
                new int[]{2, 0, 4, 63},
                new int[]{1, 50, 64, 1000});
    }

    @Test
    public void shiftedRegularIdSetTest() {
        testIdSet(IndexSet.ShiftedRegularIndexSet.class,
                new int[]{1001, 1000, 1063},
                new int[]{1, 999, 1056, 1064});
    }

    @Test
    public void jumboIdSetTest() {
        testIdSet(IndexSet.JumboIndexSet.class,
                new int[]{3, 0, 5, 64},
                new int[]{63, 94, 140, 10000});
    }

    @Test
    public void hashIdSetTest() {
        testIdSet(IndexSet.HashIndexSet.class,
                new int[]{1, 5000, 100000, 10000000},
                new int[]{0, 100020});
    }

    private void testIdSet(Class<? extends IndexSet> expectedInstance,
                           int[] values, int[] someNotContainedValues) {
        IndexSet set = IndexSet.of(values);
        Assertions.assertEquals(expectedInstance, set.getClass());
        assertValues(set, values, someNotContainedValues);

        int[] forEachValues = new int[values.length];
        IntWrapper index = new IntWrapper(0);
        set.forEach(v -> forEachValues[index.increment()] = v);
        assertArraysHasSameContent(values, forEachValues);

        set.erase(values[0]);
        Assertions.assertFalse(set.contains(values[0]));
        Arrays.stream(values).skip(1).forEach(v -> Assertions.assertTrue(set.contains(v)));
        for (int value : someNotContainedValues) {
            Assertions.assertFalse(set.contains(value));
        }

        Assertions.assertTrue(set.add(values[0]));
        Assertions.assertFalse(set.add(values[0]));
        assertValues(set, values, someNotContainedValues);
    }

    private static void assertValues(IndexSet set, int[] values, int[] someNotContainedValues) {
        for (int value : values) {
            Assertions.assertTrue(set.contains(value));
        }
        for (int value : someNotContainedValues) {
            Assertions.assertFalse(set.contains(value));
        }
    }

    private static void assertArraysHasSameContent(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            Assertions.fail("Expected array size: " + expected.length + ", actual: " + actual.length);
        }
        Collection<Integer> expectedCol = new ArrayList<>();
        Arrays.stream(expected).forEach(expectedCol::add);
        for (int val : actual) {
            if (!expectedCol.remove(val)) {
                Assertions.fail("Unexpected array element found: " + val);
            }
        }
        if (!expectedCol.isEmpty()) {
            Assertions.fail("Expected array element not found: " + expectedCol.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
    }
}