package com.pixurvival.core.livingEntity.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatModifier {

	public enum OperationType {
		ADDITIVE,
		RELATIVE;
	}

	private OperationType operationType;
	private StatType statType;
	private float value;

}
