package com.pixurvival.gdxcore;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.playerRequest.DropItemRequest;
import com.pixurvival.core.message.playerRequest.InteractStructureRequest;
import com.pixurvival.core.message.playerRequest.PlaceStructureRequest;
import com.pixurvival.core.message.playerRequest.PlayerEquipmentAbilityRequest;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.ui.ItemCraftTooltip;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldMouseProcessor extends InputAdapter {

	private @NonNull Stage worldStage;

	private boolean usingAbility = false;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer == null) {
			return false;
		}
		ItemStack heldItemStack = myPlayer.getInventory().getHeldItemStack();
		if (button == Input.Buttons.RIGHT) {
			if (heldItemStack != null && heldItemStack.getItem() instanceof StructureItem) {
				Vector2 worldPoint = screenToWorld(screenX, screenY);
				int x = MathUtils.floor(worldPoint.x);
				int y = MathUtils.floor(worldPoint.y);
				if (MapStructure.canPlace(myPlayer, PixurvivalGame.getWorld().getMap(), ((StructureItem) heldItemStack.getItem()).getStructure(), x, y)) {
					PixurvivalGame.getClient().sendAction(new PlaceStructureRequest(x, y));
				}
			} else {
				MapStructure structure = findClosest(screenX, screenY, 1);
				if (structure instanceof HarvestableMapStructure && structure.canInteract(myPlayer)) {
					PixurvivalGame.getClient().sendAction(new InteractStructureRequest(structure.getTileX(), structure.getTileY()));
				}
			}
		} else if (button == Input.Buttons.LEFT) {
			if (heldItemStack != null) {
				DropItemRequest request = new DropItemRequest((float) getActionAngle(myPlayer, screenX, screenY));
				PixurvivalGame.getClient().sendAction(request);
			} else {
				World world = PixurvivalGame.getClient().getWorld();
				com.pixurvival.core.util.Vector2 targetPosition = world.getType().isClient() ? myPlayer.getTargetPosition() : null;
				PixurvivalGame.getClient().sendAction(new PlayerEquipmentAbilityRequest(EquipmentAbilityType.WEAPON_BASE, targetPosition));
				usingAbility = true;
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT && usingAbility) {
			PixurvivalGame.getClient().sendAction(new PlayerEquipmentAbilityRequest(null, null));
			usingAbility = false;
		}

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		processMouseMoved();
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		processMouseMoved();
		return true;
	}

	private void processMouseMoved() {
		ItemCraftTooltip.getInstance().setVisible(false);
	}

	private double getActionAngle(PlayerEntity player, int screenX, int screenY) {
		Vector2 worldPoint = screenToWorld(screenX, screenY);
		return player.getPosition().angleToward(worldPoint.x, worldPoint.y);
	}

	private MapStructure findClosest(int screenX, int screenY, int radius) {
		Vector2 worldPoint = screenToWorld(screenX, screenY);
		int x = MathUtils.floor(worldPoint.x);
		int y = MathUtils.floor(worldPoint.y);
		TiledMap map = PixurvivalGame.getWorld().getMap();
		MapStructure closest = null;
		double closestDist = Double.POSITIVE_INFINITY;
		for (int dx = x - radius; dx <= x + radius; dx++) {
			for (int dy = y - radius; dy <= y + radius; dy++) {
				MapStructure structure = map.tileAt(dx, dy).getStructure();
				if (structure != null) {
					double diffX = structure.getX() - worldPoint.x;
					double diffY = structure.getY() - worldPoint.y;
					double dist = diffX * diffX + diffY * diffY;
					if (dist < closestDist) {
						closestDist = dist;
						closest = structure;
					}
				}
			}
		}
		return closest;
	}

	private Vector2 screenToWorld(int screenX, int screenY) {
		return worldStage.getViewport().unproject(new Vector2(screenX, screenY));
	}
}
