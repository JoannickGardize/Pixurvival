package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaySoundAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	private SoundPreset preset;

	@Override
	public void uniqueApply(TeamMember source, LivingEntity entity) {
		SoundEffect soundEffect = new SoundEffect(preset, entity.getPosition());
		entity.getWorld().getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
			if (p.distanceSquared(soundEffect.getPosition()) <= GameConstants.PLAYER_VIEW_DISTANCE * GameConstants.PLAYER_VIEW_DISTANCE) {
				((PlayerEntity) p).getSoundEffectsToConsume().add(soundEffect);
			}
		});
	}
}
