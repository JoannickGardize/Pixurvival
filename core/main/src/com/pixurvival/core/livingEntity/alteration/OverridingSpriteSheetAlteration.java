package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverridingSpriteSheetAlteration extends PersistentAlteration {

	private static final long serialVersionUID = 1L;

	public OverridingSpriteSheetAlteration() {
		setStackPolicy(StackPolicy.REPLACE);
	}

	@ElementReference
	private SpriteSheet spriteSheet;

	@Override
	public Object begin(TeamMember source, LivingEntity entity) {
		entity.setOverridingSpriteSheet(spriteSheet);
		return null;
	}

	@Override
	public void restore(TeamMember source, LivingEntity target, Object data) {
		target.setOverridingSpriteSheet(spriteSheet);
	}

	@Override
	public void end(TeamMember source, LivingEntity entity, Object data) {
		entity.setOverridingSpriteSheet(null);
	}
}
