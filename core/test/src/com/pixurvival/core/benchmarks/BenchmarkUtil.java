package com.pixurvival.core.benchmarks;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BenchmarkUtil {

    public static void time(String title, Runnable task) {
        long beginTime = System.currentTimeMillis();
        task.run();
        long time = System.currentTimeMillis() - beginTime;
        System.out.println(title + " : " + time + " ms");
    }
}
