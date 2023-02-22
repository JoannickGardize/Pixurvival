package com.pixurvival.core.benchmarks;

import com.pixurvival.core.util.TrigUtils;

public class TrigUtilsBenchmark {

    private static final int LOOP_SIZE = 100_000;

    public static void main(String[] args) {
        System.out.println("SIN_COUT " + TrigUtils.SIN_COUNT);
        BenchmarkUtil.time("Math.sin", () -> {
            float d = 0;
            for (int i = 0; i < LOOP_SIZE; i++) {
                d += Math.sin(i);
            }
            System.out.println(d);
        });
        BenchmarkUtil.time("TrigUtils.sin", () -> {
            float d = 0;
            for (int i = 0; i < LOOP_SIZE; i++) {
                d += TrigUtils.sin(i);
            }
            System.out.println(d);
        });
    }
}
