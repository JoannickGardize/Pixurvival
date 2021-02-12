package com.pixurvival.core.system;

import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.system.interest.TimeIntervalInterest;

import lombok.Setter;

@Setter
public class HungerSystem implements GameSystem, TimeIntervalInterest {

	@Inject
	private EntityPool entityPool;

	@Inject
	private GameMode gameMode;

	@Override
	public boolean isRequired(GameMode gameMode) {
		return gameMode.getHungerPerMinute() > 0;
	}

	@Override
	public void tick(float deltaTime) {
		entityPool.get(EntityGroup.PLAYER).forEach(e -> {
			PlayerEntity player = (PlayerEntity) e;
			player.addHungerSneaky(-(gameMode.getHungerPerSecond() * deltaTime));
			if (player.getHunger() <= 0) {
				player.takeTrueDamageSneaky(10f * deltaTime, DamageAttributes.getDefaults());
			}
		});
	}

}
