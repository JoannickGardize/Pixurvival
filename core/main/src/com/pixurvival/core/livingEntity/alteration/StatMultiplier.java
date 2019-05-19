package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;

import com.pixurvival.core.livingEntity.stats.StatType;

import lombok.Data;

@Data
public class StatMultiplier implements Serializable {

	private static final long serialVersionUID = 1L;

	private StatType statType;
	private float multiplier;
}
