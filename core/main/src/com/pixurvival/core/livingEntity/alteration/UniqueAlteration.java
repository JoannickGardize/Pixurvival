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
public abstract class UniqueAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	@Override
	public void targetedApply(TeamMember source, LivingEntity target) {
		System.out.println(source.getClass().getSimpleName());
		if (source instanceof CheckListHolder) {
			CheckListHolder holder = (CheckListHolder) source;
			System.out.println(!holder.isChecked(target));
			if (!holder.isChecked(target)) {
				holder.check(target);
				uniqueApply(source, target);
			}
		} else {
			uniqueApply(source, target);
		}
	}

	public abstract void uniqueApply(TeamMember source, LivingEntity entity);
}
