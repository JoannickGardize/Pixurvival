package com.pixurvival.core.util;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * <p>A specialized map with positive integer keys, designed for relatively short range of keys, typically indexes.</p>
 * <p>The knowledge of the maximum value allows an optimized implementation.</p>
 * <p>The behavior when passing unexpected keys in parameters (< 0 or > maxValue) is undefined.</p>
 *
 * @param <V> the value type
 */
public abstract class IndexMap<V> {

    public abstract V put(int key, V value);

    public abstract V get(int key);

    public abstract V remove(int key);

    public abstract void forEachValues(Consumer<? super V> action);

    public abstract V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);

    /**
     * Used by pixurvival's subclasses to capture a value instance before its content will change.
     */
    public V captureValueChange(V value) {
        return value;
    }

    static final int CRUNCH_THRESHOLD = 48;

    public static <V> IndexMap<V> create(int maxValue) {
        if (maxValue < CRUNCH_THRESHOLD) {
            return new ArrayIndexMap<>(maxValue);
        } else {
            return new CrushedArrayIndexMap<>(maxValue);
        }
    }

    public static <V> IndexMap<V> immutable(IndexMap<V> map) {
        return new IndexMap<V>() {
            @Override
            public V put(int key, V value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public V get(int key) {
                return map.get(key);
            }

            @Override
            public V remove(int key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forEachValues(Consumer<? super V> action) {
                map.forEachValues(action);
            }

            @Override
            public V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
                throw new UnsupportedOperationException();
            }
        };
    }

    static class ArrayIndexMap<V> extends IndexMap<V> {
        private IndexSet keys;
        private V[] values;

        public ArrayIndexMap(int maxValue) {
            keys = new IndexSet.RegularIndexSet();
            values = (V[]) new Object[maxValue + 1];
        }

        @Override
        public V put(int key, V value) {
            keys.insert(key);
            V oldValue = values[key];
            values[key] = value;
            return oldValue;
        }

        @Override
        public V get(int key) {
            return values[key];
        }

        @Override
        public V remove(int key) {
            keys.erase(key);
            V removed = values[key];
            values[key] = null;
            return removed;
        }

        @Override
        public void forEachValues(Consumer<? super V> action) {
            keys.forEach(index -> action.accept(values[index]));
        }

        /**
         * @param key
         * @param value
         * @param remappingFunction the remapping function: (oldValue, newValue) -> resultValue
         * @return
         */
        @Override
        public V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            if (keys.add(key)) {
                values[key] = value;
                return value;
            } else {
                V newValue = remappingFunction.apply(values[key], value);
                return values[key] = newValue;
            }
        }
    }

    static class CrushedArrayIndexMap<V> extends IndexMap<V> {

        private static final int CRUSH_SIZE = 32;

        private IndexSet keys;
        private V[][] values;

        public CrushedArrayIndexMap(int maxValue) {
            keys = IndexSet.create(maxValue);
            values = (V[][]) new Object[CRUSH_SIZE][];
        }

        @Override
        public V put(int key, V value) {
            keys.insert(key);
            int crushedKey = key % CRUSH_SIZE;
            int subArrayKey = key / CRUSH_SIZE;
            V[] subArray = values[crushedKey];
            if (subArray == null) {
                subArray = (V[]) new Object[Math.max(2, subArrayKey + 1)];
                subArray[subArrayKey] = value;
                values[crushedKey] = subArray;
                return null;
            } else if (subArray.length < subArrayKey + 1) {
                subArray = Arrays.copyOf(subArray, subArrayKey + 1);
                subArray[subArrayKey] = value;
                values[crushedKey] = subArray;
                return null;
            } else {
                V oldValue = subArray[subArrayKey];
                subArray[subArrayKey] = value;
                return oldValue;
            }
        }

        @Override
        public V get(int key) {
            V[] subArray = values[key % CRUSH_SIZE];
            if (subArray == null) {
                return null;
            }
            int subArrayKey = key / CRUSH_SIZE;
            if (subArray.length < subArrayKey + 1) {
                return null;
            } else {
                return subArray[subArrayKey];
            }
        }

        @Override
        public V remove(int key) {
            keys.erase(key);
            V[] subArray = values[key % CRUSH_SIZE];
            if (subArray == null) {
                return null;
            }
            int subArrayKey = key / CRUSH_SIZE;
            if (subArray.length < subArrayKey + 1) {
                return null;
            } else {
                V oldValue = subArray[subArrayKey];
                subArray[subArrayKey] = null;
                return oldValue;
            }
        }

        @Override
        public void forEachValues(Consumer<? super V> action) {
            keys.forEach(index -> action.accept(get(index)));
        }

        @Override
        public V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            if (keys.add(key)) {
                put(key, value);
                return value;
            } else {
                int subArrayKey = key / CRUSH_SIZE;
                V[] subArray = values[key % CRUSH_SIZE];
                V newValue = remappingFunction.apply(subArray[subArrayKey], value);
                return subArray[subArrayKey] = newValue;
            }
        }
    }
}
