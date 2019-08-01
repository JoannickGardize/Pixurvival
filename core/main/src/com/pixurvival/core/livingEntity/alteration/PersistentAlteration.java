package com.pixurvival.core.livingEntity.alteration;

import java.util.List;
import java.util.function.BiPredicate;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class PersistentAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	/**
	 * Enum that define how the same alteration (i.e. alteration definition from
	 * the same source instance) is stacked to a single target.
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
		STACK(List::add),
		/**
		 * The alteration is added once, and will not be replaced if already
		 * present.
		 */
		IGNORE(CollectionUtils::addIfNotPresent),
		/**
		 * The alteration is added once, and will be replaced if already
		 * present.
		 */
		REPLACE(CollectionUtils::addOrReplace);

		private BiPredicate<List<PersistentAlterationEntry>, PersistentAlterationEntry> processor;
	}

	private long duration;
	private StackPolicy stackPolicy = StackPolicy.IGNORE;

	@Override
	public void apply(TeamMember source, LivingEntity entity) {
		entity.applyPersistentAlteration(source, this);
	}

	public abstract void begin(LivingEntity entity);

	public abstract void update(LivingEntity entity);

	public abstract void end(LivingEntity entity);

}
