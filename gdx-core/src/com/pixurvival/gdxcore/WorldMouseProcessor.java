package com.pixurvival.gdxcore;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.aliveEntity.Activity;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.InteractStructureRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorldMouseProcessor extends InputAdapter {

	private Stage worldStage;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer != null && button == Input.Buttons.RIGHT && myPlayer.getActivity() == Activity.NONE) {
			MapStructure structure = findClosest(screenX, screenY, 1);
			if (structure instanceof HarvestableStructure && structure.canInteract(myPlayer)) {
				PixurvivalGame.getClient()
						.sendAction(new InteractStructureRequest(structure.getTileX(), structure.getTileY()));
			}
		}
		return true;
	}

	private MapStructure findClosest(int screenX, int screenY, int radius) {
		Vector2 worldPoint = worldStage.getViewport().unproject(new Vector2(screenX, screenY));
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
}
