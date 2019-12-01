package com.pixurvival.core.livingEntity.ability;

import lombok.Data;

@Data
public abstract class WorkAbilityData implements AbilityData {

	private long startTimeMillis;
	private long durationMillis;

	public float getProgress(long currentTime) {
		return (float) (currentTime - startTimeMillis) / (float) durationMillis;
	}
}
