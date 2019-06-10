package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.message.playerRequest.PlayerEquipmentAbilityRequest;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EquipmentAbilityProcessor implements InputActionProcessor {

	/**
	 * Static because common to all abilities
	 */
	private static boolean usingAbility = false;

	private EquipmentAbilityType equipmentAbilityType;

	@Override
	public void buttonDown() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		World world = PixurvivalGame.getClient().getWorld();
		com.pixurvival.core.util.Vector2 targetPosition = world.getType().isClient() ? myPlayer.getTargetPosition() : null;
		PixurvivalGame.getClient().sendAction(new PlayerEquipmentAbilityRequest(equipmentAbilityType, targetPosition));
		usingAbility = true;
	}

	@Override
	public void buttonUp() {
		if (usingAbility) {
			PixurvivalGame.getClient().sendAction(new PlayerEquipmentAbilityRequest(null, null));
			usingAbility = false;
		}
	}

}
