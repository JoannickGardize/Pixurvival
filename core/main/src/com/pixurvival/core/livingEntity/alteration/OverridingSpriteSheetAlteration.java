package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
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

	private SpriteSheet spriteSheet;

	@Override
	public void begin(TeamMember source, LivingEntity entity) {
		entity.setOverridingSpriteSheet(spriteSheet);
	}

	@Override
	public void update(TeamMember source, LivingEntity entity) {
	}

	@Override
	public void end(TeamMember source, LivingEntity entity) {
		entity.setOverridingSpriteSheet(null);
	}
}