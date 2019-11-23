package com.pixurvival.core.time;

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
	public double currentMomentProgress() {
		return 0;
	}

	@Override
	public long getDayCount() {
		return 0;
	}
}
