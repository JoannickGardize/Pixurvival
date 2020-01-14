package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class PersistentAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	/**
	 * Enum that define how the same alteration (i.e. alteration definition from the
	 * same source instance) is stacked to a single target.
	 * 
	 * @author SharkHendrix
	 *
	 */
	@AllArgsConstructor
	@Getter
	public enum StackPolicy {
		/**
		 * The alteration is simply stacked, without limitation.
		 */
		STACK,
		/**
		 * The alteration is added once, and will not be replaced if already present.
		 */
		IGNORE,
		/**
		 * The alteration is added once, and will be replaced if already present.
		 */
		REPLACE;

	}

	private long duration;
	private StackPolicy stackPolicy = StackPolicy.REPLACE;

	@Override
	public void targetedApply(TeamMember source, TeamMember target) {
		if (target instanceof LivingEntity) {
			((LivingEntity) target).applyPersistentAlteration(source, this);
		}
	}

	public Object begin(TeamMember source, LivingEntity target) {
		return null;
	}

	public Object update(TeamMember source, LivingEntity target, Object data) {
		return data;
	}

	public void end(TeamMember source, LivingEntity target, Object data) {
	}

}
