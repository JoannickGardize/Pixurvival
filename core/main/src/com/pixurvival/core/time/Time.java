package com.pixurvival.core.time;

import com.pixurvival.core.message.TimeSync;
import com.pixurvival.core.util.MathUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Time {

	private @Setter @Getter long timeMillis = 0;
	private float decimalAccumulator = 0;
	private @Getter @NonNull DayCycleRun dayCycle;

	private @Getter float deltaTime = 0;
	private @Getter float deltaTimeMillis = 0;
	private long synchronizeTimeCounter = 0;
	private @Getter float averagePing = 0;
	private @Getter long tickCount = 0;
	private float timeDiffAccumulator = 0;

	public void update(float deltaTimeMillis) {
		tickCount++;
		this.deltaTimeMillis = deltaTimeMillis;
		deltaTime = deltaTimeMillis / 1000f;
		long integerPart = (long) deltaTimeMillis;

		decimalAccumulator += deltaTimeMillis - integerPart;
		while (decimalAccumulator > 0.5) {
			timeMillis++;
			decimalAccumulator--;
		}
		timeMillis += integerPart;
		dayCycle.update(timeMillis);
	}

	public void synchronizeTime(TimeSync timeResponse) {
		long ping = (timeMillis - timeResponse.getRequesterTime()) / 2;
		if (averagePing == 0) {
			averagePing = ping;
		} else {
			averagePing = MathUtils.linearInterpolate(averagePing, ping, 0.2f);
		}
		long difference = timeResponse.getResponderTime() - timeMillis + ping;
		if (synchronizeTimeCounter < 20) {
			synchronizeTimeCounter++;
		}
		float toAdd = (float) difference / synchronizeTimeCounter + timeDiffAccumulator;
		int toAddInt = (int) toAdd;
		timeDiffAccumulator = toAdd - toAddInt;
		timeMillis += toAddInt;
	}

	public static long secToMillis(float secondes) {
		return (long) (secondes * 1000);
	}
}
