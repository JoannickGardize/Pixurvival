package com.pixurvival.core.time;

import java.nio.ByteBuffer;

import com.pixurvival.core.util.ByteBufferUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DayNightCycleRun implements DayCycleRun {

	private long dayDuration;
	private long nightDuration;
	private long fullCycleDuration;

	private long dayCount = 0;
	private boolean isDay = true;
	private float currentMomentProgess;

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
			currentMomentProgess = (float) dayTime / dayDuration;
		} else {
			currentMomentProgess = (float) (dayTime - dayDuration) / nightDuration;
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
	public float currentMomentProgress() {
		return currentMomentProgess;
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putLong(dayDuration);
		buffer.putLong(nightDuration);
		buffer.putLong(dayCount);
		ByteBufferUtils.putBoolean(buffer, isDay);
	}

	@Override
	public void apply(ByteBuffer buffer) {
		dayDuration = buffer.getLong();
		nightDuration = buffer.getLong();
		dayCount = buffer.getLong();
		isDay = ByteBufferUtils.getBoolean(buffer);
		fullCycleDuration = dayDuration + nightDuration;
	}

}
