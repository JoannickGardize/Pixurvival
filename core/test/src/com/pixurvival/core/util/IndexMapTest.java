package com.pixurvival.core.util;


import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IndexMapTest {

    @AllArgsConstructor
    private static class Node {
        int key;
        String value;
    }

    private static Node n(int key, String value) {
        return new Node(key, value);
    }

    private static Node[] ns(Object... keyAndValues) {
        Node[] nodes = new Node[keyAndValues.length / 2];
        for (int i = 0; i < keyAndValues.length; i += 2) {
            nodes[i / 2] = n((int) keyAndValues[i], (String) keyAndValues[i + 1]);
        }
        return nodes;
    }

    private static Stream<Arguments> createSource() {
        return Stream.of(
                Arguments.of(IndexMap.ArrayIndexMap.class, IndexMap.CRUNCH_THRESHOLD - 1),
                Arguments.of(IndexMap.CrushedArrayIndexMap.class, IndexMap.CRUNCH_THRESHOLD)
        );
    }

    @ParameterizedTest
    @MethodSource("createSource")
    void create(Class<? extends IndexMap> expectedType, int maxValue) {
        IndexMap<String> map = IndexMap.create(maxValue);
        Assertions.assertInstanceOf(expectedType, map);
    }

    private static Stream<Arguments> putSource() {
        return Stream.of(
                Arguments.of(IndexMap.CRUNCH_THRESHOLD - 1,
                        ns(2, "a", 34, "b"), new int[]{3, 15}),
                Arguments.of(100,
                        ns(0, "a", 32, "b", 56, "c"), new int[]{56 + 32, 28})
        );
    }

    @ParameterizedTest
    @MethodSource("putSource")
    void putAndGet(int maxValue, Node[] putNodes, int[] someMissingKeys) {
        IndexMap<String> map = IndexMap.create(maxValue);
        for (Node n : putNodes) {
            map.put(n.key, n.value);
        }
        for (Node n : putNodes) {
            Assertions.assertEquals(n.value, map.get(n.key));
        }
        for (int key : someMissingKeys) {
            Assertions.assertNull(map.get(key));
        }
    }

    private static Stream<Arguments> basicIndexMapSource() {
        return Stream.of(
                Arguments.of(IndexMap.CRUNCH_THRESHOLD - 1, ns(3, "a", 15, "b")),
                Arguments.of(100, ns(1, "c", 50, "a", 82, "b"))
        );
    }

    @ParameterizedTest
    @MethodSource("basicIndexMapSource")
    void remove(int maxValue, Node[] nodes) {
        IndexMap map = IndexMap.create(maxValue);
        Arrays.stream(nodes).forEach(n -> map.put(n.key, n.value));
        Arrays.stream(nodes).forEach(n -> map.remove(n.key));
        Arrays.stream(nodes).forEach(n -> Assertions.assertNull(map.get(n.key)));
    }

    @ParameterizedTest
    @MethodSource("basicIndexMapSource")
    void forEachValues(int maxValue, Node[] nodes) {
        IndexMap<String> map = IndexMap.create(maxValue);
        Arrays.stream(nodes).forEach(n -> map.put(n.key, n.value));
        List<String> expectedValues = Arrays.stream(nodes).map(n -> n.value).collect(Collectors.toList());
        List<String> actualValues = new ArrayList<>();
        map.forEachValues(s -> actualValues.add(s));
        Assertions.assertEquals(expectedValues, actualValues);
    }

    @ParameterizedTest
    @ValueSource(ints = {IndexMap.CRUNCH_THRESHOLD - 1, IndexMap.CRUNCH_THRESHOLD})
    void merge(int maxValue) {

        IndexMap<String> map = IndexMap.create(maxValue);
        Assertions.assertEquals("a", map.merge(15, "a", String::concat));
        Assertions.assertEquals("ab", map.merge(15, "b", String::concat));
    }
}