package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.team.TeamMember;

/**
 * Represents a value depending on a {@link StatSet}. It is formed by a base
 * value, and a list of {@link StatMultiplier}. The resulting value cannot be
 * negative.
 * 
 * @author SharkHendrix
 *
 */
@Getter
@Setter
public class StatAmount implements Serializable {

	private static final long serialVersionUID = 1L;

	private float base;

	private List<StatMultiplier> statMultipliers = new ArrayList<>();

	/**
	 * Returns the value of this StatAmount for the given StatSet, the value
	 * will be set to zero if the result is negative.
	 * 
	 * @param statSet
	 * @return
	 */
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
