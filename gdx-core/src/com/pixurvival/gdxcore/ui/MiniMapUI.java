package com.pixurvival.gdxcore.ui;

public class MiniMapUI extends UIWindow {

	public MiniMapUI(long myPlayerId) {
		super("miniMap");
		MiniMapActor mapActor = new MiniMapActor(myPlayerId);
		add(mapActor).fill().expand();
	}
}
