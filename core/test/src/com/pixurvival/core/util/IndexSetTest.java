package com.pixurvival.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IndexSetTest {

    @Test
    void regularIdSetTest() {
        testIdSet(IndexSet.RegularIndexSet.class,
                new int[]{2, 0, 4, 63},
                new int[]{1, 50, 64, 1000});
    }

    @Test
    void shiftedRegularIdSetTest() {
        testIdSet(IndexSet.ShiftedRegularIndexSet.class,
                new int[]{1001, 1000, 1063},
                new int[]{1, 999, 1056, 1064});
    }

    @Test
    void jumboIdSetTest() {
        testIdSet(IndexSet.JumboIndexSet.class,
                new int[]{3, 0, 5, 64},
                new int[]{63, 94, 140, 10000});
    }

    @Test
    void hashIdSetTest() {
        testIdSet(IndexSet.HashIndexSet.class,
                new int[]{1, 5000, 100000, 10000000},
                new int[]{0, 100020});
    }

    @ParameterizedTest
    @MethodSource("serializationSource")
    void serializationTest(IndexSet set, Integer[] values) {
        for (int value : values) {
            set.insert(value);
        }
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        set.write(buffer);
        buffer.position(0);
        IndexSet deserializedSet = IndexSet.read(buffer);
        Assertions.assertEquals(set.getClass(), deserializedSet.getClass());
        Set<Integer> expected = new HashSet(Arrays.asList(values));
        Set<Integer> actual = new HashSet<>();
        deserializedSet.forEach(actual::add);
        Assertions.assertEquals(expected.size(), actual.size());
        for (int i : expected) {
            Assertions.assertTrue(actual.contains(i));
        }
    }

    private static Stream<Arguments> serializationSource() {
        return Stream.of(Arguments.of(new IndexSet.EmptyIndexSet(), new Integer[]{}),
                Arguments.of(new IndexSet.RegularIndexSet(), new Integer[]{1, 5, 30, 54}),
                Arguments.of(new IndexSet.ShiftedRegularIndexSet(1000), new Integer[]{1020, 1025, 1030}),
                Arguments.of(new IndexSet.JumboIndexSet(1000), new Integer[]{0, 256, 548, 987}),
                Arguments.of(new IndexSet.HashIndexSet(), new Integer[]{20, 1000, 10000, 10049})
        );
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