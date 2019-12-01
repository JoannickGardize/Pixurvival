package com.pixurvival.core.util;

import com.pixurvival.core.World;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timer {

	private World world;
	private long startTimeMillis;
	private long durationMillis;
	private boolean loop;

	public Timer(World world, long durationMillis, boolean loop) {
		this.world = world;
		this.durationMillis = durationMillis;
		this.loop = loop;
	}

	public void reset() {
		startTimeMillis = world.getTime().getTimeMillis();
	}

	public long getElapsedTimeMillis() {
		return world.getTime().getTimeMillis() - startTimeMillis;
	}

	public float getProgress() {
		return MathUtils.clamp((float) getElapsedTimeMillis() / durationMillis, 0, 1);
	}

	public boolean update(World world) {
		if (getElapsedTimeMillis() >= durationMillis) {
			if (loop) {
				startTimeMillis += durationMillis;
			}
			return true;
		} else {
			return false;
		}
	}
}
