package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.Getter;

public class MessageWindow extends Window {

	private @Getter Label contentLabel;
	private @Getter TextButton okButton;

	public MessageWindow(String titleKey) {
		super(PixurvivalGame.getString(titleKey), PixurvivalGame.getSkin());
		Skin skin = PixurvivalGame.getSkin();

		setModal(true);
		setVisible(false);
		setMovable(false);
		setResizable(false);

		contentLabel = new Label("", skin);
		contentLabel.setWrap(true);
		okButton = new TextButton(PixurvivalGame.getString("generic.ok"), skin);
		okButton.setVisible(false);

		add(contentLabel).expand().fill().top();
		setSize(400, 200);
		row();
		add(okButton).width(100);

		okButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});

	}

	public void update(Viewport viewport) {
		setPosition(viewport.getWorldWidth() / 2 - getWidth() / 2, viewport.getWorldHeight() / 2 - getHeight() / 2);
		getCell(contentLabel).width(getWidth() - 20);
	}
}
