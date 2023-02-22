package com.pixurvival.core;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.util.MathUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EngineThread extends Thread {

    private volatile boolean running = true;
    private float frameDurationMillis = 1000f / GameConstants.FPS;
    private int maxUpdatePerFrame = 5;

    private @Setter(AccessLevel.NONE) float load;
    private float warnLoadTrigger = 100;

    public EngineThread(String name) {
        super(name);
    }

    public void setUpdatePerSecond(int updatePerSecond) {
        frameDurationMillis = 1000f / updatePerSecond;
    }

    public abstract void update(float deltaTimeMillis);

    @Override
    public void run() {
        float halfFrameDuration = frameDurationMillis / 2;
        float timeToConsume = 0;
        long startTime = System.currentTimeMillis();
        long lastUpdate = startTime;
        long now;
        while (running) {
            now = System.currentTimeMillis();
            timeToConsume += now - lastUpdate;
            lastUpdate = now;
            if (timeToConsume > maxUpdatePerFrame * frameDurationMillis) {
                timeToConsume = maxUpdatePerFrame * frameDurationMillis;
                frameSkipped();
            }
            while (timeToConsume > halfFrameDuration) {
                update(frameDurationMillis);
                timeToConsume -= frameDurationMillis;
            }
            long sleepTime = 0;
            sleepTime = Math.round(frameDurationMillis) - (System.currentTimeMillis() - now);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
            float currentLoad = (frameDurationMillis - sleepTime) / frameDurationMillis;
            load = MathUtils.linearInterpolate(load, currentLoad, 0.7f);
            if (load >= warnLoadTrigger) {
                Log.warn("Thread " + getName() + ", critical load : " + load);
            }
        }
    }

    protected void frameSkipped() {
    }
}
