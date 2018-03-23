package com.pixurvival.gdxcore;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.message.InteractStructureRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorldMouseProcessor extends InputAdapter {

	private Stage worldStage;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer != null && button == Input.Buttons.RIGHT) {
			Vector2 worldPoint = worldStage.getViewport().unproject(new Vector2(screenX, screenY));
			int x = (int) Math.floor(worldPoint.x);
			int y = (int) Math.floor(worldPoint.y);
			MapTile mapTile = PixurvivalGame.getWorld().getMap().tileAt(x, y);
			if (mapTile.getStructure() instanceof HarvestableStructure && mapTile.getStructure().canInteract(myPlayer)) {
				PixurvivalGame.getClient().sendAction(new InteractStructureRequest(x, y));
			}
		}
		return true;
	}
}
