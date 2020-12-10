package com.pixurvival.core.util;

import java.nio.ByteBuffer;

/**
 * Helper utility to write / read a sequence of long values. Uses VLQ encoding
 * and write the difference between the actual and the previous value to
 * optimize compression by minimizing the absolute value. One instance of this
 * class should only be used for one sequence of writing or reading.
 * 
 * @author SharkHendrix
 *
 */
public class LongSequenceIOHelper {

	private long previousValue = 0;

	public void write(ByteBuffer buffer, long value) {
		VarLenNumberIO.writeVarLong(buffer, value - previousValue);
		previousValue = value;
	}

	/**
	 * Write the same value of the last call to
	 * {@link LongSequenceIOHelper#write(ByteBuffer, long)} with less computational
	 * cost. Writing two time the same value is the best way to mark the end of a
	 * sequence of unique value, since it will always take one byte.
	 * 
	 * @param buffer
	 */
	public void reWriteLast(ByteBuffer buffer) {
		buffer.put((byte) 0);
	}

	public long read(ByteBuffer buffer) {
		long value = VarLenNumberIO.readVarLong(buffer) + previousValue;
		previousValue = value;
		return value;
	}
}
