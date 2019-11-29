package com.pixurvival.core.time;

import com.pixurvival.core.message.TimeResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Time {

	private @Setter @Getter long timeMillis = 0;
	private double decimalAccumulator = 0;
	private @Getter @NonNull DayCycleRun dayCycle;

	private @Getter double deltaTime = 0;
	private @Getter double deltaTimeMillis = 0;
	private long synchronizeTimeCounter = 0;
	private @Getter double averagePing = 0;
	private @Getter long tickCount = 0;

	public void update(double deltaTimeMillis) {
		tickCount++;
		this.deltaTimeMillis = deltaTimeMillis;
		deltaTime = deltaTimeMillis / 1000.0;
		long integerPart = (long) deltaTimeMillis;

		decimalAccumulator += deltaTimeMillis - integerPart;
		while (decimalAccumulator > 0.5) {
			timeMillis++;
			decimalAccumulator--;
		}
		timeMillis += integerPart;
		dayCycle.update(timeMillis);
	}

	public long synchronizeTime(TimeResponse timeResponse) {
		long ping = (timeMillis - timeResponse.getRequesterTime()) / 2;
		if (averagePing == 0) {
			averagePing = ping;
		} else {
			averagePing = averagePing * 0.80 + ping * 0.2;
		}
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
