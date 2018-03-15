package com.pixurvival.core;

import com.esotericsoftware.minlog.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EngineThread extends Thread {

	private boolean running = true;
	private double frameDurationMillis = 1000.0 / 30;
	private int maxUpdatePerFrame = 5;

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
				Log.warn("Warning, skipped time.");
			}
			while (timeToConsume > halfFrameDuration) {
				update(frameDurationMillis);
				timeToConsume -= frameDurationMillis;
			}
			long sleepTime = Math.round(frameDurationMillis) - (System.currentTimeMillis() - now);
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
					running = false;
				}
			}
		}
	}
}
