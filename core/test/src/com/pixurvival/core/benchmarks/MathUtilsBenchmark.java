package com.pixurvival.core.benchmarks;

import com.pixurvival.core.util.MathUtils;

public class MathUtilsBenchmark {

	private static final int LOOP_SIZE = 1_000_000;

	public static void main(String[] args) {

		BenchmarkUtil.time("Math.floor", () -> {
			for (int i = 0; i < LOOP_SIZE; i++) {
				Math.floor((i - LOOP_SIZE / 2) / 1000);
			}
		});

		BenchmarkUtil.time("MathUtils.floor", () -> {
			for (int i = 0; i < LOOP_SIZE; i++) {
				MathUtils.floor((i - LOOP_SIZE / 2) / 1000);
			}
		});

		BenchmarkUtil.time("Math.ceil", () -> {
			for (int i = 0; i < LOOP_SIZE; i++) {
				Math.ceil((i - LOOP_SIZE / 2) / 1000);
			}
		});

		BenchmarkUtil.time("MathUtils.ceil", () -> {
			for (int i = 0; i < LOOP_SIZE; i++) {
				MathUtils.ceil((i - LOOP_SIZE / 2) / 1000);
			}
		});
	}

}
