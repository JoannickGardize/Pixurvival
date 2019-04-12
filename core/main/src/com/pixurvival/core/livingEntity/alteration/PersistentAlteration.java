package com.pixurvival.core.livingEntity.alteration;

import java.util.List;
import java.util.function.BiPredicate;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class PersistentAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	@AllArgsConstructor
	@Getter
	public enum StackPolicy {
		STACK(List::add),
		IGNORE(CollectionUtils::addIfNotPresent),
		REPLACE(CollectionUtils::addOrReplace);

		private BiPredicate<List<PersistentAlterationEntry>, PersistentAlterationEntry> processor;
	}

	private double duration;
	private StackPolicy stackPolicy = StackPolicy.IGNORE;

	@Override
	public void apply(Object source, LivingEntity entity) {
		entity.applyPersistentAlteration(source, this);
	}

	public abstract void begin(LivingEntity entity);

	public abstract void update(LivingEntity entity);

	public abstract void end(LivingEntity entity);

}
