package com.pixurvival.core.livingEntity.stats;

import java.io.Serializable;

import lombok.Data;

@Data
public class StatModifier implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum OperationType {
		ADDITIVE,
		RELATIVE;
	}

	private StatType statType;
	private OperationType operationType;
	private float value;

}
