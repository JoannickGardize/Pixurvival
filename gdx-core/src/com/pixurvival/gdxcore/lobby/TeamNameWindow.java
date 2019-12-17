package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.UILabel;

public class TeamNameWindow extends Window {

	public TeamNameWindow() {
		super(PixurvivalGame.getString("lobby.addTeam"), PixurvivalGame.getSkin());
		setResizable(false);
		setModal(true);

		TextField teamNameField = new TextField("", PixurvivalGame.getSkin());
		TextButton addButton = new TextButton("generic.add", PixurvivalGame.getSkin());
		TextButton cancelButton = new TextButton("generic.cancel", PixurvivalGame.getSkin());
		
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				returnValue = teamNameField.getText();
				setVisible(false);
			}
		});
		
		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});
		
		add(new UILabel("lobby.teamNameQuestion", Color.WHITE)).expand().fill();
		row();
		add(teamNameField).expandX().align(Align.center);
		row();
		add()
		
	}

}
