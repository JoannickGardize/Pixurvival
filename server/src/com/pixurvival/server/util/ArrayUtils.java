package com.pixurvival.server.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class ArrayUtils {

    @SafeVarargs
    public byte[] append(byte[] array, byte... toAppend) {
        byte[] result = Arrays.copyOf(array, array.length + toAppend.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;
    }
}
