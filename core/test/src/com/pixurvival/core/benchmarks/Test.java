package com.pixurvival.core.benchmarks;

import java.util.HashMap;
import java.util.Map;

public class Test {
	public static void main(String[] args) {

		Map<Long, Long> map = new HashMap<>();

		map.put(1000L, 1000L);
		map.put(45L, 45l);
		map.put(50L, 50l);
		map.put(51L, 51l);

		map.put(0L, 0l);
		map.put(1L, 1l);
		map.put(3000L, 3000L);

		for (Long l : map.values()) {
			System.out.println(l);
		}
	}
}
