package com.pixurvival.core.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.item.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ByteBufferUtils {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	public static final int BUFFER_SIZE = 8192;

	private static ThreadLocal<ByteBuffer> bufferLocal = ThreadLocal.withInitial(() -> {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		buffer.mark();
		return buffer;
	});

	public static ByteBuffer getThreadSafeInstance() {
		return bufferLocal.get();
	}

	public static void writeElementOrNull(ByteBuffer buffer, IdentifiedElement element) {
		if (element == null) {
			buffer.putShort((short) -1);
		} else {
			buffer.putShort((short) element.getId());
		}
	}

	public static <T extends IdentifiedElement> T readElementOrNull(ByteBuffer buffer, List<T> elementList) {
		short id = buffer.getShort();
		if (id == -1) {
			return null;
		} else {
			return elementList.get(id);
		}
	}

	public static void writeItemOrNull(ByteBuffer buffer, ItemStack itemStack) {
		if (itemStack == null) {
			buffer.putShort((short) -1);
		} else {
			buffer.putShort((short) itemStack.getItem().getId());
		}
	}

	public static ItemStack readItemOrNullAsItemStack(ByteBuffer buffer, List<Item> itemList) {
		short id = buffer.getShort();
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

	public static void putString(ByteBuffer buffer, String s) {
		byte[] data = s.getBytes(CHARSET);
		buffer.putInt(data.length);
		buffer.put(data);
	}

	public static String getString(ByteBuffer buffer) {
		byte[] data = new byte[buffer.getInt()];
		buffer.get(data);
		return new String(data, CHARSET);
	}

	public static void putBytes(ByteBuffer buffer, byte[] bytes) {
		buffer.putInt(bytes.length);
		buffer.put(bytes);
	}

	public static byte[] getBytes(ByteBuffer buffer) {
		int length = buffer.getInt();
		byte[] result = new byte[length];
		buffer.get(new byte[length]);
		return result;
	}
}
