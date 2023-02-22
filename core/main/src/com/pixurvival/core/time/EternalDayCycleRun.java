package com.pixurvival.core.time;

import java.nio.ByteBuffer;

public class EternalDayCycleRun implements DayCycleRun {

    @Override
    public boolean update(long time) {
        return false;
    }

    @Override
    public boolean isDay() {
        return true;
    }

    @Override
    public float currentMomentProgress() {
        return 0;
    }

    @Override
    public long getDayCount() {
        return 0;
    }

    @Override
    public void write(ByteBuffer buffer) {
        // Nothing
    }

    @Override
    public void apply(ByteBuffer buffer) {
        // Nothing
    }
}
