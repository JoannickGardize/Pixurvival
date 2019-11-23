package com.pixurvival.core.time;

import lombok.Getter;

@Getter
public class DayNightCycleRun implements DayCycleRun {

	private long dayDuration;
	private long nightDuration;
	private long fullCycleDuration;

	private long dayCount = 0;
	private boolean isDay = true;
	private double currentMomentProgess;

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
			currentMomentProgess = (double) dayTime / dayDuration;
		} else {
			currentMomentProgess = (double) (dayTime - dayDuration) / nightDuration;
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
	public double currentMomentProgress() {
		return currentMomentProgess;
	}

}
