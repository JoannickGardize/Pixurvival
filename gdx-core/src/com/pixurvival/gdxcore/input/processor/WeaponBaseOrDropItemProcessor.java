package com.pixurvival.gdxcore.input.processor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.message.playerRequest.DropItemRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;

public class WeaponBaseOrDropItemProcessor implements InputActionProcessor {

	private EquipmentAbilityProcessor weaponBaseAbilityProcessor = new EquipmentAbilityProcessor(EquipmentAbilityType.WEAPON_BASE);
	private boolean dropingItem = false;

	@Override
	public void buttonDown() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		ItemStack heldItemStack = myPlayer.getInventory().getHeldItemStack();
		if (heldItemStack != null) {
			DropItemRequest request = new DropItemRequest(getActionAngle(myPlayer, Gdx.input.getX(), Gdx.input.getY()));
			PixurvivalGame.getClient().sendAction(request);
			dropingItem = true;
		} else {
			weaponBaseAbilityProcessor.buttonDown();
			dropingItem = false;
		}
	}

	@Override
	public void buttonUp() {
		if (!dropingItem) {
			weaponBaseAbilityProcessor.buttonUp();
		}
	}

	private float getActionAngle(PlayerEntity player, int screenX, int screenY) {
		Vector2 worldPoint = screenToWorld(screenX, screenY);
		return player.getPosition().angleToward(worldPoint.x, worldPoint.y);
	}

	private Vector2 screenToWorld(int screenX, int screenY) {
		return WorldScreen.getWorldStage().getViewport().unproject(new Vector2(screenX, screenY));
	}
}
