package com.pixurvival.core.livingEntity.alteration;

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
		if (source instanceof CheckListHolder) {
			CheckListHolder holder = (CheckListHolder) source;
			if (!holder.isChecked(entity)) {
				holder.check(entity);
				uniqueApply(source, entity);
			}
		} else {
			uniqueApply(source, entity);
		}
	}

	public abstract void uniqueApply(TeamMember source, LivingEntity entity);
}
