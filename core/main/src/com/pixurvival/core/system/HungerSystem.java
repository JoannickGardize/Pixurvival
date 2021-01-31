package com.pixurvival.core.system;

import com.pixurvival.core.World;
import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.system.interest.TimeIntervalInterest;

public class HungerSystem extends BaseSystem implements TimeIntervalInterest {

	private EntityPool entityPool;
	private GameMode gameMode;

	public HungerSystem(World world) {
		super(world);
		this.entityPool = world.getEntityPool();
		this.gameMode = world.getGameMode();
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
