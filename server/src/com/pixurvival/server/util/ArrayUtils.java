package com.pixurvival.server.util;

import java.util.Arrays;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayUtils {

	@SafeVarargs
	public byte[] append(byte[] array, byte... toAppend) {
		byte[] result = Arrays.copyOf(array, array.length + toAppend.length);
		System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
		return result;
	}
}
