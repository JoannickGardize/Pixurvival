package com.pixurvival.gdxcore.menu;

import java.util.function.Consumer;

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
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.UILabel;

public class InputWindow extends Window {

	private TextField inputField;

	public InputWindow(String titleKey, String validateKey, Consumer<String> validateAction) {
		super(PixurvivalGame.getString(titleKey), PixurvivalGame.getSkin());
		setResizable(false);
		setModal(true);

		inputField = new TextField("", PixurvivalGame.getSkin());
		TextButton addButton = new TextButton(PixurvivalGame.getString(validateKey), PixurvivalGame.getSkin());
		TextButton cancelButton = new TextButton(PixurvivalGame.getString("generic.cancel"), PixurvivalGame.getSkin());

		inputField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					validateAction.accept(inputField.getText());
					setVisible(false);
				}
				return true;
			}
		});
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				validateAction.accept(inputField.getText());
				setVisible(false);
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
		add(inputField).expandX().align(Align.center);
		row();
		HorizontalGroup buttonGroup = new HorizontalGroup().space(2);
		buttonGroup.addActor(addButton);
		buttonGroup.addActor(cancelButton);
		add(buttonGroup);
		pack();
	}

	public void setVisible(String inputValue) {
		if (getStage() != null) {
			inputField.setText(inputValue);
			getStage().setKeyboardFocus(inputField);
		}
		super.setVisible(true);
	}

	public void update(Viewport viewport) {
		setPosition(viewport.getWorldWidth() / 2 - getWidth() / 2, viewport.getWorldHeight() / 2 - getHeight() / 2);
	}
}
