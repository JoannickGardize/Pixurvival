package com.pixurvival.core;

import com.pixurvival.core.message.TimeResponse;

import lombok.Getter;

@Getter
public class Time {

	public static final double timeOffsetAlpha = 0.6;
	public static final double timeOffsetAlpha2 = 1.0 - timeOffsetAlpha;

	private long timeMillis = 0;

	private double deltaTime = 0;
	private double deltaTimeMillis = 0;
	private double decimalAccumulator = 0;
	private long synchronizeTimeCounter = 0;

	public void update(double deltaTimeMillis) {
		this.deltaTimeMillis = deltaTimeMillis;
		deltaTime = deltaTimeMillis / 1000.0;
		long integerPart = (long) deltaTimeMillis;
		decimalAccumulator += deltaTimeMillis - integerPart;
		while (decimalAccumulator > 0.5) {
			timeMillis++;
			decimalAccumulator--;
		}
		timeMillis += integerPart;
	}

	public long synchronizeTime(TimeResponse timeResponse) {
		long ping = (timeMillis - timeResponse.getRequesterTime()) / 2;
		long difference = timeResponse.getResponderTime() - (timeMillis - ping);
		if (synchronizeTimeCounter < 20) {
			synchronizeTimeCounter++;
		}
		timeMillis += Math.round((double) difference / synchronizeTimeCounter);
		long absDiff = Math.abs(difference);
		if (absDiff > 100) {
			return 1000;
		} else if (absDiff > 50) {
			return 5000;
		} else {
			return 10_000;
		}
	}

	public static long secToMillis(double secondes) {
		return (long) (secondes * 1000);
	}
}
