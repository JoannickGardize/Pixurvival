package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.UILabel;

public class TeamNameWindow extends Window {

	private TextField teamNameField;

	public TeamNameWindow() {
		super(PixurvivalGame.getString("lobby.addTeam"), PixurvivalGame.getSkin());
		setResizable(false);
		setModal(true);

		teamNameField = new TextField("", PixurvivalGame.getSkin());
		TextButton addButton = new TextButton(PixurvivalGame.getString("generic.add"), PixurvivalGame.getSkin());
		TextButton cancelButton = new TextButton(PixurvivalGame.getString("generic.cancel"), PixurvivalGame.getSkin());

		teamNameField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					sendNewTeam();
				}
				return true;
			}
		});
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sendNewTeam();
			}
		});

		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});

		defaults().pad(2);
		add(new UILabel("lobby.teamNameQuestion", Color.WHITE)).expand().fill();
		row();
		add(teamNameField).expandX().align(Align.center);
		row();
		HorizontalGroup buttonGroup = new HorizontalGroup();
		buttonGroup.addActor(addButton);
		buttonGroup.addActor(cancelButton);
		add(buttonGroup);
		pack();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible && getStage() != null) {
			teamNameField.setText("");
			getStage().setKeyboardFocus(teamNameField);
		}
		super.setVisible(visible);
	}

	public void update(Viewport viewport) {
		setPosition(viewport.getWorldWidth() / 2 - getWidth() / 2, viewport.getWorldHeight() / 2 - getHeight() / 2);
	}

	private void sendNewTeam() {
		PixurvivalGame.getClient().send(new CreateTeamRequest(teamNameField.getText()));
		setVisible(false);
	}
}
