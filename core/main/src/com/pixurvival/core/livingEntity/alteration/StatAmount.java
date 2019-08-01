package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatAmount implements Serializable {

	private static final long serialVersionUID = 1L;

	private float base;

	private List<StatMultiplier> statMultipliers = new ArrayList<>();

	public float getValue(StatSet statSet) {
		float result = base;
		for (StatMultiplier multiplier : statMultipliers) {
			result += statSet.getValue(multiplier.getStatType()) * multiplier.getMultiplier();
		}
		return Math.max(result, 0);
	}

	public float getValue(TeamMember sourceProvider) {
		return getValue(sourceProvider.getStats());
	}
}
