package com.pixurvival.gdxcore.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.gdxcore.PixurvivalGame;

public class DebugInfosActors extends Actor {

	private List<Function<World, String>> lines = new ArrayList<>();

	public DebugInfosActors() {
		lines.add(world -> "FPS : " + Gdx.graphics.getFramesPerSecond());
		lines.add(world -> "Position : " + (world.getMyPlayer() == null ? "?" : world.getMyPlayer().getPosition()));
		for (EntityGroup entityGroup : EntityGroup.values()) {
			lines.add(world -> entityGroup.name() + " : " + world.getEntityPool().get(entityGroup).size());
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		World world = PixurvivalGame.getWorld();
		if (world == null) {
			return;
		}
		BitmapFont font = PixurvivalGame.getOverlayFont();
		font.setColor(Color.WHITE);
		for (int i = 0; i < lines.size(); i++) {
			String str = lines.get(i).apply(world);
			font.draw(batch, str, 10, Gdx.graphics.getHeight() - 10 - i * font.getLineHeight());
		}
	}
}
