package com.pixurvival.gdxcore.ui;

import com.pixurvival.gdxcore.PixurvivalGame;

public class MiniMapUI extends UIWindow {

	public MiniMapUI(long myPlayerId) {
		super("Mini Map", PixurvivalGame.getSkin());
		MiniMapActor mapActor = new MiniMapActor(myPlayerId);
		mapActor.setSize(200, 200);
		add(mapActor);
		pack();
	}

}
