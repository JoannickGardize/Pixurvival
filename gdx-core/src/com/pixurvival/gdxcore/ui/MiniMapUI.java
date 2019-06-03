package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;

public class MiniMapUI extends UIWindow {

	private MiniMapActor mapActor;

	public MiniMapUI(long myPlayerId) {
		super("miniMap");
		mapActor = new MiniMapActor(myPlayerId);
		add(mapActor).fill().expand();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		mapActor.setPosition(0, 0);
		mapActor.setSize(getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}
}
