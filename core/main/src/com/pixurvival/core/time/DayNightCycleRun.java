package com.pixurvival.core.time;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.Getter;

@Getter
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

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<DayNightCycleRun> {

		@Override
		public void write(Kryo kryo, Output output, DayNightCycleRun object) {
			output.writeLong(object.dayDuration);
			output.writeLong(object.nightDuration);
			output.writeLong(object.dayCount);
			output.writeBoolean(object.isDay());
		}

		@Override
		public DayNightCycleRun read(Kryo kryo, Input input, Class<DayNightCycleRun> type) {
			DayNightCycleRun result = new DayNightCycleRun(input.readLong(), input.readLong());
			result.dayCount = input.readLong();
			result.isDay = input.readBoolean();
			return result;
		}

	}
}
