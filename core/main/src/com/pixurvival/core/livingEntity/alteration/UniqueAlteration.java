package com.pixurvival.core.livingEntity.alteration;

import java.util.Collection;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

/**
 * An alteration that can be applied only one time per entity from the same
 * source (e.g. {@link EffectEntity}).
 * 
 * @author SharkHendrix
 *
 */
public abstract class UniqueAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	@Override
	public void apply(TeamMember source, LivingEntity entity) {
		Collection<Object> checkList = ((CheckListHolder) source).getCheckList();
		if (!checkList.contains(entity)) {
			checkList.add(entity);
			uniqueApply(source, entity);
		}
	}

	public abstract void uniqueApply(TeamMember source, LivingEntity entity);
}
