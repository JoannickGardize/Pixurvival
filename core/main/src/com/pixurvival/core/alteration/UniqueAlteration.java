package com.pixurvival.core.alteration;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.team.TeamMember;

/**
 * An alteration that can be applied only one time per entity from the same
 * {@link CheckListHolder} source (e.g. {@link EffectEntity}).
 * 
 * @author SharkHendrix
 *
 */
public abstract class UniqueAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	@Override
	public void targetedApply(TeamMember source, TeamMember target) {
		if (source instanceof CheckListHolder) {
			CheckListHolder holder = (CheckListHolder) source;
			if (!holder.check(target)) {
				uniqueApply(source, target);
			}
		} else {
			uniqueApply(source, target);
		}
	}

	public abstract void uniqueApply(TeamMember source, TeamMember entity);
}