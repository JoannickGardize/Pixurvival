package com.pixurvival.core.util;

import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KryoUtils {

	public static void writeUniqueClassList(Kryo kryo, Output output, List<?> list) {
		int size = list.size();
		output.writeShort(size);
		for (int i = 0; i < size; i++) {
			kryo.writeObject(output, list.get(i));
		}
	}

	public static void writeUnspecifiedClassList(Kryo kryo, Output output, List<?> list) {
		int size = list.size();
		output.writeShort(size);
		for (int i = 0; i < size; i++) {
			kryo.writeClassAndObject(output, list.get(i));
		}
	}

	public static <T> void readUniqueClassList(Kryo kryo, Input input, List<? super T> list, Class<T> type) {
		int size = input.readShort();
		for (int i = 0; i < size; i++) {
			list.add(kryo.readObject(input, type));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void readUnspecifiedClassList(Kryo kryo, Input input, List<?> list) {
		int size = input.readShort();
		for (int i = 0; i < size; i++) {
			((List) list).add(kryo.readClassAndObject(input));
		}
	}
}
