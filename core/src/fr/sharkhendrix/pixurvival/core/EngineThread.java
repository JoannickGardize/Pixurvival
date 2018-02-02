package fr.sharkhendrix.pixurvival.core;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class EngineThread extends Thread {

	private @Getter @Setter boolean running = true;
	private @Getter List<World> worlds = new ArrayList<>();
	private double frameDuration = 1000.0 / 30;
	private int maxUpdatePerFrame = 5;

	public void setUpdatePerSecond(int updatePerSecond) {
		frameDuration = 1000.0 / updatePerSecond;
	}

	@Override
	public void run() {
		double halfFrameDuration = frameDuration / 2;
		double timeToConsume = 0;
		long startTime = System.currentTimeMillis();
		long lastUpdate = startTime;
		long now = startTime;
		long count = 0;
		long time = startTime;
		while (running) {
			now = System.currentTimeMillis();
			timeToConsume += now - lastUpdate;
			lastUpdate = now;
			if (timeToConsume > maxUpdatePerFrame * frameDuration) {
				timeToConsume = maxUpdatePerFrame * frameDuration;
			}
			int nowCount = 0;
			while (timeToConsume > halfFrameDuration) {
				worlds.forEach(w -> w.update(frameDuration));
				timeToConsume -= frameDuration;
				count++;
				nowCount++;
			}
			System.out.println(nowCount + ", " + count / ((System.currentTimeMillis() - time) / 1000.0));
			long sleepTime = Math.round(frameDuration) - (System.currentTimeMillis() - now);
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					running = false;
				}
			}
		}
	}

	public static void main(String[] args) {
		new EngineThread().start();
	}
}
