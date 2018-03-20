package com.pixurvival.core.util;

import com.pixurvival.core.World;

public class Timer {

	private double timer;
	private double duration;

	public Timer(double duration) {
		this.duration = duration;
	}

	public void reset() {
		timer = 0;
	}

	public boolean update(World world) {
		timer += world.getTime().getDeltaTime();
		if (timer >= duration) {
			timer -= duration;
			return true;
		} else {
			return false;
		}
	}
}
