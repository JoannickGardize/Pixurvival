package com.pixurvival.core.alteration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a formula depending on a {@link StatSet}. It is formed by a base
 * value, and a list of {@link StatMultiplier}. The resulting value cannot be
 * negative.
 * 
 * @author SharkHendrix
 *
 */
@Getter
@Setter
public class StatFormula implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Useful to reference this StatAmount in translation texts.
	 */
	private long id = -1;

	private float base;

	@Valid
	private List<StatMultiplier> statMultipliers = new ArrayList<>();

	/**
	 * Returns the value of this StatAmount for the given StatSet, the value will be
	 * set to zero if the result is negative.
	 * 
	 * @param statSet
	 * @return
	 */
	public float getValue(StatSet statSet) {
		float result = base;
		for (StatMultiplier multiplier : statMultipliers) {
			result += statSet.getValue(multiplier.getStatType()) * multiplier.getMultiplier();
		}
		return result;
	}

	public float getValue(TeamMember sourceProvider) {
		return getValue(sourceProvider.getStats());
	}
}
