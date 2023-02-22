package com.pixurvival.core.time;

import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DayNightCycleRun implements DayCycleRun {

    private long dayDuration;
    private long nightDuration;
    private long fullCycleDuration;

    private long dayCount = 0;
    private boolean isDay = true;
    private float currentMomentProgess;

    public DayNightCycleRun(long dayDuration, long nightDuration) {
        this.dayDuration = dayDuration;
        this.nightDuration = nightDuration;
        fullCycleDuration = dayDuration + nightDuration;
    }

    @Override
    public boolean update(long time) {
        long newDayCount = time / fullCycleDuration;
        long dayTime = time % fullCycleDuration;
        boolean newIsDay = dayTime <= dayDuration;
        if (newIsDay) {
            currentMomentProgess = (float) dayTime / dayDuration;
        } else {
            currentMomentProgess = (float) (dayTime - dayDuration) / nightDuration;
        }
        if (newDayCount != dayCount || newIsDay != isDay) {
            dayCount = newDayCount;
            isDay = newIsDay;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDay() {
        return isDay;
    }

    @Override
    public float currentMomentProgress() {
        return currentMomentProgess;
    }

    @Override
    public void write(ByteBuffer buffer) {
        VarLenNumberIO.writePositiveVarLong(buffer, dayDuration);
        VarLenNumberIO.writePositiveVarLong(buffer, nightDuration);
        VarLenNumberIO.writePositiveVarLong(buffer, dayCount);
        ByteBufferUtils.putBoolean(buffer, isDay);
    }

    @Override
    public void apply(ByteBuffer buffer) {
        dayDuration = VarLenNumberIO.readPositiveVarLong(buffer);
        nightDuration = VarLenNumberIO.readPositiveVarLong(buffer);
        dayCount = VarLenNumberIO.readPositiveVarLong(buffer);
        isDay = ByteBufferUtils.getBoolean(buffer);
        fullCycleDuration = dayDuration + nightDuration;
    }

}
