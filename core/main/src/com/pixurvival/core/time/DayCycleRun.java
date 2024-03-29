package com.pixurvival.core.time;

import java.nio.ByteBuffer;

/**
 * Base interface for day / night cycle management.
 *
 * @author SharkHendrix
 */
public interface DayCycleRun {

    /**
     * @param time current world time in milliseconds.
     * @return true if the current day moment switched from day to night, or
     * from night to day
     */
    boolean update(long time);

    /**
     * @return true if this is the day, false if this is the night
     */
    boolean isDay();

    /**
     * @return the current moment (day or night) progress, 0 mean the moment
     * just started, and values close to 1 mean the moment will change
     * soon
     */
    float currentMomentProgress();

    /**
     * @return number of days since the beginning of the game, starting to zero
     */
    long getDayCount();

    void write(ByteBuffer buffer);

    void apply(ByteBuffer buffer);
}
