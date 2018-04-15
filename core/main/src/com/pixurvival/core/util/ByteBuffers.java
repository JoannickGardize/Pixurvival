package com.pixurvival.core.util;

import java.nio.ByteBuffer;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ByteBuffers {

	private static ThreadLocal<ByteBuffer> bufferLocal = ThreadLocal.withInitial(() -> {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		buffer.mark();
		return buffer;
	});

	public static ByteBuffer get() {
		return bufferLocal.get();
	}
}
