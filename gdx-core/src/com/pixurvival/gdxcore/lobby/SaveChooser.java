package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.pixurvival.core.WorldSerialization;
import com.pixurvival.gdxcore.PixurvivalGame;

public class SaveChooser extends Table {

	private List<String> savesList = new List<>(PixurvivalGame.getSkin());

	public SaveChooser() {
		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
		defaults().expandX().pad(2).fill();
		add(new Label(PixurvivalGame.getString("lobby.saves"), PixurvivalGame.getSkin(), "default", Color.WHITE));
		row();
		add(savesList);
	}

	public void update() {
		savesList.setItems(WorldSerialization.listSaves());
	}

	public String getSelectedSave() {
		return savesList.getSelected();
	}

}
