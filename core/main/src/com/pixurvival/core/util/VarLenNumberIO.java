package com.pixurvival.core.util;

import java.nio.ByteBuffer;

// Remove this copied class when SharkSerialization is used instead of Kryo

/**
 * Provides methods to serialize / deserialize primitive numbers with as few
 * bytes as possible for relatively small and common values.
 * <p>
 * The strategy is to only consider the significant data (ignore leading zeros).
 * The first bits are used as a "variable size flag" to announce the number of
 * bytes to use. This flag consists of succession of "1" finished with a "0".
 * The length of this flag is the number of bytes to use, including the rest of
 * the byte containing this flag, so the flag "0" means 1 byte, "10" means 2
 * bytes, "110" means 3 bytes, and so on.
 * <p>
 * In the worst case, if the number of bytes to use is greater than the number
 * of bytes of the primitive type, the eventual rest of the flag byte is lost,
 * and is ignored. In this case, the "0" at the end of the flag is not required
 * because we already know that this is the maximum length.
 * <p>
 * Unless common variable-length quantity (VLQ) encoding, there is only one
 * packed "variable size" flag at the beginning of the first byte, instead of
 * one bit flag in each byte, in this way, the algorithm requires fewer bitwise
 * operations and so runs faster.
 * <p>
 * The number of bytes used vary between 1 byte in the best case and
 * {@code size of the primitive + 1} bytes in the worst case.
 * <p>
 * Every methods of this class has a "positive" version which is recommended for
 * positive-only values (e.g. an array's length), they will require fewer bytes,
 * while negative values will always be in the worst case scenario.
 * Additionally, these positive versions run slightly faster.
 *
 * @author Joannick Gardize
 */
public class VarLenNumberIO {

    private static final int ONE_BYTE_DATA_SIZE = 8 * 1 - 1;
    private static final int TWO_BYTES_DATA_SIZE = 8 * 2 - 2;
    private static final int THREE_BYTES_DATA_SIZE = 8 * 3 - 3;
    private static final int FOUR_BYTES_DATA_SIZE = 8 * 4 - 4;
    private static final int FIVE_BYTES_DATA_SIZE = 8 * 5 - 5;
    private static final int SIX_BYTES_DATA_SIZE = 8 * 6 - 6;
    private static final int SEVEN_BYTES_DATA_SIZE = 8 * 7 - 7;
    private static final int EIGHT_BYTES_DATA_SIZE = 8 * 8 - 8;

    private static final byte BYTES_2_FLAG = (byte) 0b10000000;
    private static final byte BYTES_3_FLAG = (byte) 0b11000000;
    private static final byte BYTES_4_FLAG = (byte) 0b11100000;
    private static final byte BYTES_5_FLAG = (byte) 0b11110000;
    private static final byte BYTES_6_FLAG = (byte) 0b11111000;
    private static final byte BYTES_7_FLAG = (byte) 0b11111100;
    private static final byte BYTES_8_FLAG = (byte) 0b11111110;
    private static final byte BYTES_9_FLAG = (byte) 0b11111111;

    private static final int BYTES_2_SHORT_FLAG = BYTES_2_FLAG << 8;
    private static final int BYTES_4_INT_FLAG = BYTES_4_FLAG << 24;
    private static final int BYTES_6_SHORT_FLAG = BYTES_6_FLAG << 8;
    private static final long BYTES_8_LONG_FLAG = (long) BYTES_8_FLAG << 56;

    public static void writeVarInt(ByteBuffer buffer, int i) {
        writePositiveVarInt(buffer, i << 1 ^ i >> 31);
    }

    public static int readVarInt(ByteBuffer buffer) {
        int i = readPositiveVarInt(buffer);
        return i >>> 1 ^ -(i & 1);
    }

    public static void writePositiveVarInt(ByteBuffer buffer, int i) {
        if (i >> ONE_BYTE_DATA_SIZE == 0) {
            buffer.put((byte) i);
        } else if (i >> TWO_BYTES_DATA_SIZE == 0) {
            buffer.putShort((short) (i | BYTES_2_SHORT_FLAG));
        } else if (i >> THREE_BYTES_DATA_SIZE == 0) {
            buffer.put((byte) (i >> 16 | BYTES_3_FLAG));
            buffer.putShort((short) i);
        } else if (i >> FOUR_BYTES_DATA_SIZE == 0) {
            buffer.putInt(i | BYTES_4_INT_FLAG);
        } else {
            buffer.put(BYTES_5_FLAG);
            buffer.putInt(i);
        }
    }

    public static int readPositiveVarInt(ByteBuffer buffer) {
        byte b = buffer.get();
        if ((b & BYTES_2_FLAG) == 0) {
            return b;
        } else if ((b & BYTES_3_FLAG) == BYTES_2_FLAG) {
            return (b & ~BYTES_2_FLAG) << 8 | buffer.get() & 0xFF;
        } else if ((b & BYTES_4_FLAG) == BYTES_3_FLAG) {
            return (b & ~BYTES_3_FLAG) << 16 | buffer.getShort() & 0xFFFF;
        } else if ((b & BYTES_5_FLAG) == BYTES_4_FLAG) {
            return (b & ~BYTES_4_FLAG) << 24 | buffer.get() << 16 & 0xFF0000 | buffer.getShort() & 0xFFFF;
        } else {
            return buffer.getInt();
        }
    }

    public static void writeVarLong(ByteBuffer buffer, long i) {
        writePositiveVarLong(buffer, i << 1L ^ i >> 63L);
    }

    public static long readVarLong(ByteBuffer buffer) {
        long i = readPositiveVarLong(buffer);
        return i >>> 1L ^ -(i & 1L);
    }

    public static void writePositiveVarLong(ByteBuffer buffer, long l) {
        if (l >> ONE_BYTE_DATA_SIZE == 0) {
            buffer.put((byte) l);
        } else if (l >> TWO_BYTES_DATA_SIZE == 0) {
            buffer.putShort((short) (l | BYTES_2_SHORT_FLAG));
        } else if (l >> THREE_BYTES_DATA_SIZE == 0) {
            buffer.put((byte) (l >> 16 | BYTES_3_FLAG));
            buffer.putShort((short) l);
        } else if (l >> FOUR_BYTES_DATA_SIZE == 0) {
            buffer.putInt((int) l | BYTES_4_INT_FLAG);
        } else if (l >> FIVE_BYTES_DATA_SIZE == 0) {
            buffer.put((byte) (l >> 32 | BYTES_5_FLAG));
            buffer.putInt((int) l);
        } else if (l >> SIX_BYTES_DATA_SIZE == 0) {
            buffer.putShort((short) (l >> 32 | BYTES_6_SHORT_FLAG));
            buffer.putInt((int) l);
        } else if (l >> SEVEN_BYTES_DATA_SIZE == 0) {
            buffer.put((byte) (l >> 48 | BYTES_7_FLAG));
            buffer.putShort((short) (l >> 32));
            buffer.putInt((int) l);
        } else if (l >> EIGHT_BYTES_DATA_SIZE == 0) {
            buffer.putLong(l | BYTES_8_LONG_FLAG);
        } else {
            buffer.put(BYTES_9_FLAG);
            buffer.putLong(l);
        }
    }

    public static long readPositiveVarLong(ByteBuffer buffer) {
        byte b = buffer.get();
        if ((b & BYTES_2_FLAG) == 0) {
            return b;
        } else if ((b & BYTES_3_FLAG) == BYTES_2_FLAG) {
            return (b & ~BYTES_2_FLAG) << 8 | buffer.get() & 0xFF;
        } else if ((b & BYTES_4_FLAG) == BYTES_3_FLAG) {
            return (b & ~BYTES_3_FLAG) << 16 | buffer.getShort() & 0xFFFF;
        } else if ((b & BYTES_5_FLAG) == BYTES_4_FLAG) {
            return (b & ~BYTES_4_FLAG) << 24 | buffer.get() << 16 & 0xFF0000 | buffer.getShort() & 0xFFFF;
        } else if ((b & BYTES_6_FLAG) == BYTES_5_FLAG) {
            return (long) (b & ~BYTES_5_FLAG) << 32 | buffer.getInt() & 0xFFFFFFFFL;
        } else if ((b & BYTES_7_FLAG) == BYTES_6_FLAG) {
            return (long) (b & ~BYTES_6_FLAG) << 40 | (long) buffer.get() << 32 & 0xFF00000000L | buffer.getInt() & 0xFFFFFFFFL;
        } else if ((b & BYTES_8_FLAG) == BYTES_7_FLAG) {
            return (long) (b & ~BYTES_7_FLAG) << 48 | (long) buffer.getShort() << 32 & 0xFFFF00000000L | buffer.getInt() & 0xFFFFFFFFL;
        } else if ((b & BYTES_9_FLAG) == BYTES_8_FLAG) {
            return (long) buffer.get() << 48 & 0xFF000000000000L | (long) buffer.getShort() << 32 & 0xFFFF00000000L | buffer.getInt() & 0xFFFFFFFFL;
        } else {
            return buffer.getLong();
        }
    }
}