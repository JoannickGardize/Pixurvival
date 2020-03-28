package com.pixurvival.core.time;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
			averagePing = MathUtils.linearInterpolate(averagePing, ping, 0.1f);
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

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<Time> {

		@Override
		public void write(Kryo kryo, Output output, Time object) {
			kryo.writeClassAndObject(output, object.dayCycle);
			output.writeLong(object.timeMillis);
			output.writeFloat(object.decimalAccumulator);
		}

		@Override
		public Time read(Kryo kryo, Input input, Class<Time> type) {
			Time time = new Time((DayCycleRun) kryo.readClassAndObject(input));
			time.timeMillis = input.readLong();
			time.decimalAccumulator = input.readFloat();
			return time;
		}
	}
}
