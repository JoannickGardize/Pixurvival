package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;

public class HeldItemStackActor extends Actor {

	private ItemStackDrawer itemStackDrawer;

	public HeldItemStackActor() {
		itemStackDrawer = new ItemStackDrawer(this, 0);
		setSize(40, 40);
		setTouchable(Touchable.disabled);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer != null) {
			Vector2 mousePos = getStage().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			setPosition(mousePos.x - getWidth() / 2, mousePos.y - getHeight() / 2);
			itemStackDrawer.setItemStack(myPlayer.getHeldItemStack());
			itemStackDrawer.draw(batch);
		} else {
			itemStackDrawer.setItemStack(null);
		}
	}
}
