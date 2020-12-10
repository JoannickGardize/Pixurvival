package com.pixurvival.core.benchmarks;

import java.nio.ByteBuffer;

import com.pixurvival.core.util.LongSequenceIOHelper;

public class Test {
	public static void main(String[] args) {

		ByteBuffer buff = ByteBuffer.allocate(100);
		LongSequenceIOHelper l = new LongSequenceIOHelper();
		buff.put((byte) 0);
		buff.flip();
		l = new LongSequenceIOHelper();
		System.out.println(l.read(buff));
	}
}
