package com.pixurvival.core.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.item.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ByteBufferUtils {

	public static final int BUFFER_SIZE = 8192;

	private static ThreadLocal<ByteBuffer> bufferLocal = ThreadLocal.withInitial(() -> {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		buffer.mark();
		return buffer;
	});

	public static ByteBuffer getThreadSafeInstance() {
		return bufferLocal.get();
	}

	public static void writeElementOrNull(ByteBuffer buffer, NamedIdentifiedElement element) {
		if (element == null) {
			VarLenNumberIO.writeVarInt(buffer, -1);
		} else {
			VarLenNumberIO.writeVarInt(buffer, element.getId());
		}
	}

	public static <T extends NamedIdentifiedElement> T readElementOrNull(ByteBuffer buffer, List<T> elementList) {
		int id = VarLenNumberIO.readVarInt(buffer);
		if (id == -1) {
			return null;
		} else {
			return elementList.get(id);
		}
	}

	public static void writeItemOrNull(ByteBuffer buffer, ItemStack itemStack) {
		if (itemStack == null) {
			VarLenNumberIO.writeVarInt(buffer, -1);
		} else {
			VarLenNumberIO.writeVarInt(buffer, itemStack.getItem().getId());
		}
	}

	public static ItemStack readItemOrNullAsItemStack(ByteBuffer buffer, List<Item> itemList) {
		int id = VarLenNumberIO.readVarInt(buffer);
		if (id == -1) {
			return null;
		} else {
			return new ItemStack(itemList.get(id));
		}
	}

	public static void putBoolean(ByteBuffer buffer, boolean b) {
		buffer.put(b ? (byte) 1 : 0);
	}

	public static boolean getBoolean(ByteBuffer buffer) {
		return buffer.get() == 1;
	}

	public static void putBooleans(ByteBuffer buffer, boolean b1, boolean b2) {
		buffer.put((byte) ((b1 ? 1 : 0) | (b2 ? 2 : 0)));
	}

	public static byte getBooleansMask(ByteBuffer buffer) {
		return buffer.get();
	}

	public static boolean getBoolean1(byte mask) {
		return (mask & 1) == 1;
	}

	public static boolean getBoolean2(byte mask) {
		return (mask & 2) == 2;
	}

	public static void putString(ByteBuffer buffer, String s) {
		byte[] data = s.getBytes(StandardCharsets.UTF_8);
		VarLenNumberIO.writePositiveVarInt(buffer, data.length);
		buffer.put(data);
	}

	public static String getString(ByteBuffer buffer) {
		byte[] data = new byte[VarLenNumberIO.readPositiveVarInt(buffer)];
		buffer.get(data);
		return new String(data, StandardCharsets.UTF_8);
	}

	public static void putBytes(ByteBuffer buffer, byte[] bytes) {
		VarLenNumberIO.writePositiveVarInt(buffer, bytes.length);
		buffer.put(bytes);
	}

	public static byte[] getBytes(ByteBuffer buffer) {
		int length = VarLenNumberIO.readPositiveVarInt(buffer);
		byte[] result = new byte[length];
		buffer.get(result);
		return result;
	}

	public static void writeFutureTime(ByteBuffer buffer, World world, long time) {
		VarLenNumberIO.writePositiveVarLong(buffer, time - world.getTime().getSerializationContextTime());
	}

	public static long readFutureTime(ByteBuffer buffer, World world) {
		return VarLenNumberIO.readPositiveVarLong(buffer) + world.getTime().getSerializationContextTime();
	}

	public static void writePastTime(ByteBuffer buffer, World world, long time) {
		VarLenNumberIO.writePositiveVarLong(buffer, world.getTime().getSerializationContextTime() - time);
	}

	public static long readPastTime(ByteBuffer buffer, World world) {
		return world.getTime().getSerializationContextTime() - VarLenNumberIO.readPositiveVarLong(buffer);
	}
}
