package com.pixurvival.core.livingEntity.alteration;

import java.util.Collection;

import com.pixurvival.core.entity.SourceProvider;
import com.pixurvival.core.livingEntity.LivingEntity;

public abstract class UniqueAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	@Override
	public void apply(SourceProvider source, LivingEntity entity) {
		Collection<Object> checkList = ((CheckListHolder) source).getCheckList();
		if (!checkList.contains(entity)) {
			checkList.add(entity);
			uniqueApply(source, entity);
		}
	}

	public abstract void uniqueApply(SourceProvider source, LivingEntity entity);
}
