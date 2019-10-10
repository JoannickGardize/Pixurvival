package com.pixurvival.gdxcore.benchmark;

import com.badlogic.gdx.math.MathUtils;
import com.pixurvival.core.benchmarks.BenchmarkUtil;

public class Test {
	public static void main(String[] args) {
		BenchmarkUtil.time("sqrt", () -> {
			double d = 0;
			for (double i = 0; i < 10_000; i++) {
				d += Math.cos(i);
			}
			System.out.println(d);
		});

		BenchmarkUtil.time("invsqrt", () -> {
			float d = 0;
			for (float i = 0; i < 10_000; i++) {
				d += MathUtils.cos(i);
			}
			System.out.println(d);
		});
	}
}
