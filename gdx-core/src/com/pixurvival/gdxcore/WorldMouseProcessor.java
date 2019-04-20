package com.pixurvival.gdxcore;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.item.Item.StructureDetails;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.playerRequest.DropItemRequest;
import com.pixurvival.core.message.playerRequest.InteractStructureRequest;
import com.pixurvival.core.message.playerRequest.PlaceStructureRequest;
import com.pixurvival.core.message.playerRequest.PlayerEquipmentAbilityRequest;
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
			if (heldItemStack != null && heldItemStack.getItem().getDetails() instanceof StructureDetails) {
				Vector2 worldPoint = screenToWorld(screenX, screenY);
				int x = (int) Math.floor(worldPoint.x);
				int y = (int) Math.floor(worldPoint.y);
				if (MapStructure.canPlace(myPlayer, PixurvivalGame.getWorld().getMap(), ((StructureDetails) heldItemStack.getItem().getDetails()).getStructure(), x, y)) {
					PixurvivalGame.getClient().sendAction(new PlaceStructureRequest(x, y));
				}
			} else {
				MapStructure structure = findClosest(screenX, screenY, 1);
				if (structure instanceof HarvestableStructure && structure.canInteract(myPlayer)) {
					PixurvivalGame.getClient().sendAction(new InteractStructureRequest(structure.getTileX(), structure.getTileY()));
				}
			}
		} else if (button == Input.Buttons.LEFT) {
			if (heldItemStack != null) {
				DropItemRequest request = new DropItemRequest((float) getActionAngle(myPlayer, screenX, screenY));
				PixurvivalGame.getClient().sendAction(request);
			} else {
				PixurvivalGame.getClient().sendAction(new PlayerEquipmentAbilityRequest(EquipmentAbilityType.WEAPON_BASE));
				usingAbility = true;
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT && usingAbility) {
			PixurvivalGame.getClient().sendAction(new PlayerEquipmentAbilityRequest(null));
			usingAbility = false;
		}

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		processMouseMoved(screenX, screenY);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		processMouseMoved(screenX, screenY);
		return true;
	}

	private void processMouseMoved(int screenX, int screenY) {
		ItemCraftTooltip.getInstance().setVisible(false);
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer != null) {
			Vector2 worldPoint = screenToWorld(screenX, screenY);
			myPlayer.getTargetPosition().set(worldPoint.x, worldPoint.y);
		}
	}

	private double getActionAngle(PlayerEntity player, int screenX, int screenY) {
		Vector2 worldPoint = screenToWorld(screenX, screenY);
		return player.getPosition().angleToward(worldPoint.x, worldPoint.y);
	}

	private MapStructure findClosest(int screenX, int screenY, int radius) {
		Vector2 worldPoint = screenToWorld(screenX, screenY);
		int x = (int) Math.floor(worldPoint.x);
		int y = (int) Math.floor(worldPoint.y);
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
