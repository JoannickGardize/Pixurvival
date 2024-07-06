package com.pixurvival.core.util;

import com.esotericsoftware.kryo.util.IntMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.function.IntConsumer;

/**
 * <p>Set of positive integer values (including zero), designed for storing indexes within a relatively short range.</p>
 * <p>Returns an optimized implementation according to the provided information.</p>
 * <p>In the best case, the set is backed by a simple long bitmask, in the worst case,
 * it is backed by an {@link IntMap}.</p>
 * <p>The implementations are not guaranteed to be safe with wrong parameters,
 * especially when trying to add out of range values.</p>
 */
public abstract class IndexSet {

    /**
     * @param value
     * @return true if the set changed
     * (in other words, true if the set didn't contain the value already).
     */
    public abstract boolean add(int value);

    /**
     * Same as add but faster because of the lack of return value.
     *
     * @param value
     */
    public abstract void insert(int value);

    public abstract void erase(int value);

    public abstract boolean contains(int value);

    public abstract void forEach(IntConsumer action);

    public abstract void write(ByteBuffer buffer);

    public static IndexSet read(ByteBuffer buffer) {
        byte type = buffer.get();
        switch (type) {
            case EmptyIndexSet.SERIALIZATION_ID:
                return EMPTY;
            case RegularIndexSet.SERIALIZATION_ID:
                return RegularIndexSet.read(buffer);
            case ShiftedRegularIndexSet.SERIALIZATION_ID:
                return ShiftedRegularIndexSet.read(buffer);
            case JumboIndexSet.SERIALIZATION_ID:
                return JumboIndexSet.read(buffer);
            case HashIndexSet.SERIALIZATION_ID:
                return HashIndexSet.read(buffer);
            default:
                throw new RuntimeException("Unknown IndexSet type: " + type);
        }
    }

    /**
     * Create an IdSet that is able to store values between 0 and maxValue (inclusive).
     *
     * @param maxValue the maximum value that the set can store
     * @return
     */
    public static IndexSet create(int maxValue) {
        return create(0, maxValue);
    }

    public static IndexSet create(int minValue, int maxValue) {
        if (maxValue < 64) {
            return new RegularIndexSet();
        } else if (maxValue - minValue < 64) {
            return new ShiftedRegularIndexSet(minValue);
        } else if (maxValue < 8192) {
            return new JumboIndexSet(maxValue);
        } else {
            return new HashIndexSet();
        }
    }

    /**
     * @param values
     * @return an IdSet initialized with the given values,
     * and able to store values between the min and the max value provided (inclusive).
     */
    public static IndexSet of(int... values) {
        if (values.length == 0) {
            return EMPTY;
        }
        int min = values[0];
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            int val = values[i];
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
        }
        IndexSet set = create(min, max);
        for (int value : values) {
            set.insert(value);
        }
        return set;
    }

    private static final EmptyIndexSet EMPTY = new EmptyIndexSet();

    static class EmptyIndexSet extends IndexSet {

        static final byte SERIALIZATION_ID = 0;

        @Override
        public boolean add(int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void insert(int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void erase(int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(int value) {
            return false;
        }

        @Override
        public void forEach(IntConsumer action) {
        }

        @Override
        public void write(ByteBuffer buffer) {
            buffer.put(SERIALIZATION_ID);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    static class RegularIndexSet extends IndexSet {

        static final byte SERIALIZATION_ID = 1;

        private long values = 0;

        @Override
        public boolean add(int value) {
            long oldValue = values;
            insert(value);
            return oldValue != values;
        }

        @Override
        public void insert(int value) {
            values |= (1L << value);
        }

        @Override
        public void erase(int value) {
            values &= ~(1L << value);
        }

        @Override
        public boolean contains(int value) {
            return value < 64 && (values & (1L << value)) != 0;
        }

        @Override
        public void forEach(IntConsumer action) {
            long unseen = values;
            long lastValue;
            while (unseen != 0) {
                lastValue = unseen & -unseen;
                unseen -= lastValue;
                action.accept(Long.numberOfTrailingZeros(lastValue));
            }
        }

        @Override
        public void write(ByteBuffer buffer) {
            buffer.put(SERIALIZATION_ID);
            VarLenNumberIO.writeVarLong(buffer, values);
        }

        public static IndexSet read(ByteBuffer buffer) {
            return new RegularIndexSet(VarLenNumberIO.readVarLong(buffer));
        }
    }

    @AllArgsConstructor
    static class ShiftedRegularIndexSet extends IndexSet {

        static final byte SERIALIZATION_ID = 2;

        private int shift;
        private long values = 0;

        public ShiftedRegularIndexSet(int minValue) {
            shift = minValue;
        }

        @Override
        public boolean add(int value) {
            long oldValue = values;
            insert(value);
            return oldValue != values;
        }

        @Override
        public void insert(int value) {
            values |= (1L << (value - shift));
        }

        @Override
        public void erase(int value) {
            values &= ~(1L << (value - shift));
        }

        @Override
        public boolean contains(int value) {
            int shifted = value - shift;
            return shifted >= 0 && shifted < 64 && (values & (1L << shifted)) != 0;
        }

        @Override
        public void forEach(IntConsumer action) {
            long unseen = values;
            long lastValue;
            while (unseen != 0) {
                lastValue = unseen & -unseen;
                unseen -= lastValue;
                action.accept(Long.numberOfTrailingZeros(lastValue) + shift);
            }
        }

        @Override
        public void write(ByteBuffer buffer) {
            buffer.put(SERIALIZATION_ID);
            VarLenNumberIO.writeVarInt(buffer, shift);
            VarLenNumberIO.writeVarLong(buffer, values);
        }

        public static IndexSet read(ByteBuffer buffer) {
            return new ShiftedRegularIndexSet(VarLenNumberIO.readVarInt(buffer), VarLenNumberIO.readVarLong(buffer));
        }
    }

    @AllArgsConstructor
    static class JumboIndexSet extends IndexSet {

        static final byte SERIALIZATION_ID = 3;

        private long[] values;

        public JumboIndexSet(int maxValue) {
            values = new long[(maxValue + 64) >>> 6];
        }

        @Override
        public boolean add(int value) {
            int index = value >>> 6;
            long oldValue = values[index];
            values[index] |= (1L << value);
            return oldValue != values[index];
        }

        @Override
        public void insert(int value) {
            values[value >>> 6] |= (1L << value);
        }

        @Override
        public void erase(int value) {
            values[value >>> 6] &= ~(1L << value);
        }

        @Override
        public boolean contains(int value) {
            int arrayIndex = value >>> 6;
            return arrayIndex < values.length && (values[arrayIndex] & (1L << value)) != 0;
        }

        @Override
        public void forEach(IntConsumer action) {
            long unseen = values[0];
            int unseenIndex = 0;
            long lastValue;
            int lastIndex;
            while (true) {
                while (unseen == 0 && unseenIndex < values.length - 1) {
                    unseen = values[++unseenIndex];
                }
                if (unseen == 0) {
                    break;
                }
                lastValue = unseen & -unseen;
                lastIndex = unseenIndex;
                unseen -= lastValue;
                action.accept((lastIndex << 6)
                        + Long.numberOfTrailingZeros(lastValue));
            }
        }

        @Override
        public void write(ByteBuffer buffer) {
            buffer.put(SERIALIZATION_ID);
            ByteBufferUtils.putLongs(buffer, values);
        }

        public static IndexSet read(ByteBuffer buffer) {
            return new JumboIndexSet(ByteBufferUtils.getLongs(buffer));
        }
    }

    static class HashIndexSet extends IndexSet {

        static final byte SERIALIZATION_ID = 4;

        private static Object valueMock = new Object();

        private IntMap<Object> map = new IntMap<>();

        @Override
        public boolean add(int value) {
            return map.put(value, valueMock) == null;
        }

        @Override
        public void insert(int value) {
            add(value);
        }

        @Override
        public void erase(int value) {
            map.remove(value);
        }

        @Override
        public boolean contains(int value) {
            return map.containsKey(value);
        }

        @Override
        public void forEach(IntConsumer action) {
            IntMap.Keys keys = map.keys();
            while (keys.hasNext) {
                action.accept(keys.next());
            }
        }

        @Override
        public void write(ByteBuffer buffer) {
            buffer.put(SERIALIZATION_ID);
            VarLenNumberIO.writePositiveVarInt(buffer, map.size);
            forEach(key -> VarLenNumberIO.writePositiveVarInt(buffer, key));
        }

        public static IndexSet read(ByteBuffer buffer) {
            HashIndexSet result = new HashIndexSet();
            int size = VarLenNumberIO.readPositiveVarInt(buffer);
            for (int i = 0; i < size; i++) {
                result.insert(VarLenNumberIO.readPositiveVarInt(buffer));
            }
            return result;
        }
    }
}
