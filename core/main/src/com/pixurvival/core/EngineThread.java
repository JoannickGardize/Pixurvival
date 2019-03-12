package com.pixurvival.core;

import com.pixurvival.core.util.MathUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EngineThread extends Thread {

	private boolean running = true;
	private double frameDurationMillis = 1000.0 / 30;
	private int maxUpdatePerFrame = 5;

	private @Setter(AccessLevel.NONE) double load;

	public EngineThread(String name) {
		super(name);
	}

	public void setUpdatePerSecond(int updatePerSecond) {
		frameDurationMillis = 1000.0 / updatePerSecond;
	}

	public abstract void update(double deltaTimeMillis);

	@Override
	public void run() {
		double halfFrameDuration = frameDurationMillis / 2;
		double timeToConsume = 0;
		long startTime = System.currentTimeMillis();
		long lastUpdate = startTime;
		long now = startTime;
		while (running) {
			now = System.currentTimeMillis();
			timeToConsume += now - lastUpdate;
			lastUpdate = now;
			if (timeToConsume > maxUpdatePerFrame * frameDurationMillis) {
				timeToConsume = maxUpdatePerFrame * frameDurationMillis;
				frameSkipped();
			}
			int updateCount = 0;
			while (timeToConsume > halfFrameDuration) {
				update(frameDurationMillis);
				timeToConsume -= frameDurationMillis;
				updateCount++;
			}
			long sleepTime = 0;
			if (updateCount == 1) {
				sleepTime = Math.round(frameDurationMillis) - (System.currentTimeMillis() - now);
				if (sleepTime > 0) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
						running = false;
					}
				}
			}
			double currentLoad = (frameDurationMillis - sleepTime) / frameDurationMillis;
			load = MathUtils.linearInterpolate(load, currentLoad, 0.7);
		}
	}

	protected void frameSkipped() {
	}
}
