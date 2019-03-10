package com.pixurvival.core.livingEntity.ability;

import lombok.Data;

@Data
public abstract class WorkAbilityData implements AbilityData {

	private double startTime;
	private double duration;

	public double getProgress(double currentTime) {
		return (currentTime - startTime) / duration;
	}
}
