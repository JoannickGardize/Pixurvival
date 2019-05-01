package com.pixurvival.client;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityProxy;
import com.pixurvival.core.message.playerRequest.UpdateTargetPositionRequest;

public class TargetPositionUpdateManager implements IClientGamePlugin {

	public static final long TARGET_POSITION_UPDATE_TIME_INTERVAL = 50;

	private long previousTargetPositionUpdateTime;

	@Override
	public void initialize(ClientGame client) {
		previousTargetPositionUpdateTime = 0;
	}

	@Override
	public void update(ClientGame client) {
		PlayerEntity myPlayer = client.getMyPlayer();
		long currentTime = client.getWorld().getTime().getTimeMillis();
		if (myPlayer != null && myPlayer.getCurrentAbility() instanceof EquipmentAbilityProxy && currentTime - previousTargetPositionUpdateTime >= TARGET_POSITION_UPDATE_TIME_INTERVAL) {
			client.sendAction(new UpdateTargetPositionRequest(myPlayer.getTargetPosition()));
			previousTargetPositionUpdateTime = currentTime;
		}
	}

}
