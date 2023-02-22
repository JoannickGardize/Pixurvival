package com.pixurvival.core.benchmarks;

public class SqrtChrono {

    public static void main(String[] args) {
        BenchmarkUtil.time("sqrt", () -> {
            double k = 0;
            for (double i = 0; i < 10_000_000; i++) {
                k += Math.sqrt(i);
            }
            System.out.println(k);
        });
        BenchmarkUtil.time("mul", () -> {
            double k = 0;
            for (double i = 0; i < 10_000_000; i++) {
                k += i * i;
            }
            System.out.println(k);
        });

    }
}
