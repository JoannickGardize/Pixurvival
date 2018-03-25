package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.gdxcore.PixurvivalGame;

public class MiniMapUI extends UIWindow {

	public MiniMapUI(long myPlayerId) {
		super("Mini Map", PixurvivalGame.getSkin());
		Table table = new Table();
		MiniMapActor mapActor = new MiniMapActor(myPlayerId);
		table.add(mapActor).fill().expand().size(200);
		add(table);
		pack();

	}

}
