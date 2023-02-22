package com.pixurvival.server;

import com.pixurvival.core.util.IntWrapper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class NetworkStatisticsReporter implements NetworkActivityListener {

    private static final NumberFormat numberFormat = new DecimalFormat("0.##");

    private Map<PlayerGameSession, Map<Class<?>, IntWrapper>> counters = new HashMap<>();

    @Override
    public void sent(PlayerGameSession connection, Object object, int size) {
        counters.computeIfAbsent(connection, p -> new HashMap<>()).computeIfAbsent(object.getClass(), c -> new IntWrapper(0)).add(size);
    }

    public void report(long elapsedTime) {
        counters.keySet().removeIf(p -> !p.getConnection().isConnected());
        counters.forEach((s, counter) -> {
            System.out.print(s.getConnection());
            counter.forEach((type, count) -> {
                System.out.print(" - ");
                System.out.print(type.getSimpleName());
                System.out.print(" : ");
                float seconds = elapsedTime / 1000f;
                System.out.print(numberFormat.format(count.getValue() / seconds / 1024f));
                System.out.print(" kB/s");
                count.setValue(0);
            });
            System.out.println("\n");
        });
    }
}
