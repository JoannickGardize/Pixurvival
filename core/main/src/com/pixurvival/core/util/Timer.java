package com.pixurvival.core.util;

import com.pixurvival.core.World;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timer {

	private double timer;
	private double duration;
	private boolean loop;

	public Timer(double duration, boolean loop) {
		this.duration = duration;
		this.loop = loop;
	}

	public void reset() {
		timer = 0;
	}

	public double getProgress() {
		return MathUtils.clamp(timer / duration, 0, 1);
	}

	public boolean update(World world) {
		timer += world.getTime().getDeltaTime();
		if (timer >= duration) {
			if (loop) {
				timer -= duration;
			}
			return true;
		} else {
			return false;
		}
	}
}
