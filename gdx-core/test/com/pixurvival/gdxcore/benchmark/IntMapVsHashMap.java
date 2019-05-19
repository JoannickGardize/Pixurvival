package com.pixurvival.gdxcore.benchmark;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.IntMap;
import com.pixurvival.core.benchmarks.BenchmarkUtil;

public class IntMapVsHashMap {

	public static void main(String[] args) {
		IntMap<Integer> intMap = new IntMap<>();

		intMap.put(0, 0);
		intMap.put(1, 1);
		intMap.put(2, 2);
		intMap.put(3, 3);

		BenchmarkUtil.time("intMap.get", () -> {
			for (int i = 0; i < 1_000_000; i++) {
				intMap.get(i % 4);
			}
		});

		Map<Integer, Integer> hashMap = new HashMap<>();

		hashMap.put(0, 0);
		hashMap.put(1, 1);
		hashMap.put(2, 2);
		hashMap.put(3, 3);

		BenchmarkUtil.time("hashMap.get", () -> {
			for (int i = 0; i < 1_000_000; i++) {
				hashMap.get(i % 4);
			}
		});

		BenchmarkUtil.time("intMap.foreach", () -> {
			for (int i = 0; i < 1_000_000; i++) {
				intMap.values().forEach(entry -> {
				});
			}
		});

		BenchmarkUtil.time("hashMap.foreach", () -> {
			for (int i = 0; i < 1_000_000; i++) {
				hashMap.values().forEach(value -> {
				});
			}
		});

		BenchmarkUtil.time("intMap.for", () -> {
			for (int i = 0; i < 1_000_000; i++) {
				int x = 0;
				for (Integer value : intMap.values()) {
					x++;
				}
			}
		});

		BenchmarkUtil.time("hashMap.for", () -> {
			for (int i = 0; i < 1_000_000; i++) {
				int x = 0;
				for (Integer value : hashMap.values()) {
					x++;
				}
			}
		});
	}
}
