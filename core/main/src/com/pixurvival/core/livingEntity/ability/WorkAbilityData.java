package com.pixurvival.core.livingEntity.ability;

import lombok.Data;

@Data
public abstract class WorkAbilityData implements AbilityData {

	private long startTimeMillis;
	private long durationMillis;

	public double getProgress(long currentTime) {
		return (double) (currentTime - startTimeMillis) / (double) durationMillis;
	}
}
