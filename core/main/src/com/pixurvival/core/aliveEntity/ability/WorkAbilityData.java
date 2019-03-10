package com.pixurvival.core.aliveEntity.ability;

import lombok.Getter;
import lombok.Setter;

public abstract class WorkAbilityData implements AbilityData {

	private @Getter @Setter double startTime;
	private @Getter double duration;

	public WorkAbilityData(double duration) {
		this.duration = duration;
	}
}
